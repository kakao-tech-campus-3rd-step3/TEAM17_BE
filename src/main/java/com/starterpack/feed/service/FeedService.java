package com.starterpack.feed.service;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;
import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.feed.dto.FeedBookmarkResponseDto;
import com.starterpack.feed.dto.FeedCreateRequestDto;
import com.starterpack.feed.dto.FeedLikeResponseDto;
import com.starterpack.feed.dto.FeedResponseDto;
import com.starterpack.feed.dto.InteractionStatusResponseDto;
import com.starterpack.feed.dto.FeedUpdateRequestDto;
import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.entity.FeedBookmark;
import com.starterpack.feed.entity.FeedLike;
import com.starterpack.feed.repository.FeedBookmarkRepository;
import com.starterpack.feed.repository.FeedLikeRepository;
import com.starterpack.feed.repository.FeedRepository;
import com.starterpack.feed.specification.FeedSpecification;
import com.starterpack.hashtag.entity.Hashtag;
import com.starterpack.hashtag.service.HashtagService;
import com.starterpack.member.entity.Member;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final CategoryRepository categoryRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final EntityManager entityManager;
    private final FeedBookmarkRepository feedBookmarkRepository;
    private final HashtagService hashtagService;

    @Transactional
    public Feed addFeed(
            Member member,
            FeedCreateRequestDto createDto) {
        Category category = getCategory(createDto.categoryId());

        List<Hashtag> hashtags = hashtagService.resolveHashtags(createDto.hashtagNames());

        Feed feed = Feed.builder()
                .user(member)
                .description(createDto.description())
                .imageUrl(createDto.imageUrl())
                .category(category)
                .hashtags(hashtags)
                .build();

        hashtagService.incrementUsageCount(new HashSet<>(hashtags));

        return feedRepository.save(feed);
    }

    @Transactional(readOnly = true)
    public FeedResponseDto getFeed(
            Member member,
            Long feedId
    ) {
        Feed feed = getFeedByIdWithDetails(feedId);

        if (member == null) {
            return FeedResponseDto.forAnonymous(feed);
        } else {
            boolean isLiked = feedLikeRepository.existsByFeedAndMember(feed, member);
            boolean isBookmarked = feedBookmarkRepository.existsByFeedAndMember(feed, member);

            return FeedResponseDto.forMember(feed, InteractionStatusResponseDto.of(isLiked, isBookmarked));
        }
    }

    @Transactional(readOnly = true)
    public Feed getFeedByAdmin(Long feedId) {
        return getFeedByIdWithDetails(feedId);
    }

    @Transactional(readOnly = true)
    public Page<FeedResponseDto> getAllFeeds(Member member, Pageable pageable) {
        Page<Feed> feedPage = feedRepository.findAll(pageable);

        if (member == null) { //비로그인
            return feedPage.map(FeedResponseDto::forAnonymous);
        } else { //로그인
            Map<Long, InteractionStatusResponseDto> statusMap = getFeedInteractionStatusMap(member, feedPage.getContent());

            return feedPage.map(feed -> {
                InteractionStatusResponseDto statusDto = statusMap.getOrDefault(feed.getId(), InteractionStatusResponseDto.anonymousStatus());
                return FeedResponseDto.forMember(feed, statusDto);
            });
        }
    }

    @Transactional
    public void updateFeed(
            Long feedId,
            Member member,
            FeedUpdateRequestDto updateDto
    ){
        Feed feed = getFeedByIdWithDetails(feedId);

        feed.validateOwner(member);

        Category category = getCategory(updateDto.categoryId());

        feed.update(updateDto.description(),
                updateDto.imageUrl(),
                category);

        List<Hashtag> hashtags = hashtagService.resolveHashtags(updateDto.hashtagNames());

        Feed.HashtagUpdateResult result = feed.updateHashtag(hashtags);

        hashtagService.incrementUsageCount(result.added());
        hashtagService.decrementUsageCount(result.removed());
    }

    @Transactional
    public void deleteFeed(Long feedId, Member member) {
        Feed feed = getFeedByIdWithDetails(feedId);

        feed.validateOwner(member);

        List<Hashtag> hashtags = feed.getHashtags();
        hashtagService.decrementUsageCount(new HashSet<>(hashtags));

        feedRepository.delete(feed);
    }

    @Transactional
    public FeedLikeResponseDto toggleFeedLike(Long feedId, Member liker) {
        Feed feed = getFeedById(feedId);

        boolean exists = feedLikeRepository.existsByFeedAndMember(feed, liker);

        if (exists) {
            feedLikeRepository.deleteByFeedAndMember(feed, liker);
            feedRepository.decrementLikeCount(feedId);
        } else {
            feedLikeRepository.save(new FeedLike(feed, liker));
            feedRepository.incrementLikeCount(feedId);
        }

        entityManager.refresh(feed);

        return FeedLikeResponseDto.of(feed.getLikeCount(), !exists);
    }

    @Transactional(readOnly = true)
    public Page<Member> getFeedLikers(Long feedId, Pageable pageable) {
        Feed feed = getFeedById(feedId);
        Page<FeedLike> feedLikes = feedLikeRepository.findByFeed(feed, pageable);

        return feedLikes.map(FeedLike::getMember);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<Feed> searchFeeds(String keyword, Long categoryId, Pageable pageable) {
        Specification<Feed> spec = FeedSpecification.hasKeyword(keyword)
                .and(FeedSpecification.hasCategory(categoryId));

        return feedRepository.findAll(spec, pageable);
    }

    @Transactional
    public void updateFeedByAdmin(Long feedId, FeedUpdateRequestDto request) {
        Feed feed = getFeedById(feedId);

        Category category = getCategory(request.categoryId());


        feed.update(request.description(), request.imageUrl(), category);
    }

    @Transactional
    public void deleteFeedByAdmin(Long feedId) {
        Feed feed = getFeedById(feedId);
        feedRepository.delete(feed);
    }

    private Feed getFeedByIdWithDetails(Long feedId) {
        return feedRepository.findWithDetailsById(feedId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEED_NOT_FOUND));
    }

    private Feed getFeedById(Long feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEED_NOT_FOUND));
    }

    @Transactional
    public FeedBookmarkResponseDto toggleFeedBookmark(Long feedId, Member member) {
        Feed feed = getFeedById(feedId);

        boolean exists = feedBookmarkRepository.existsByFeedAndMember(feed, member);

        if (exists) {
            feedBookmarkRepository.deleteByFeedAndMember(feed, member);
            feedRepository.decrementBookmarkCount(feedId);
        } else {
            feedBookmarkRepository.save(new FeedBookmark(feed, member));
            feedRepository.incrementBookmarkCount(feedId);
        }

        return FeedBookmarkResponseDto.of(!exists);
    }

    private Map<Long, InteractionStatusResponseDto> getFeedInteractionStatusMap(Member member, List<Feed> feeds) {
        if (feeds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> feedIds = feeds.stream().map(Feed::getId).toList();

        Set<Long> likedFeedIds = feedLikeRepository.findFeedIdsByMemberAndFeedIds(member, feedIds);
        Set<Long> bookmarkedFeedIds = feedBookmarkRepository.findFeedIdsByMemberAndFeedIds(member, feedIds);

        return feeds.stream()
                .collect(Collectors.toMap(
                        Feed::getId,
                        feed -> InteractionStatusResponseDto.of(
                                likedFeedIds.contains(feed.getId()),
                                bookmarkedFeedIds.contains(feed.getId())
                        )
                ));
    }
}
