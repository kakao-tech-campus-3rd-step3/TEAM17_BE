package com.starterpack.feed.service;

import com.starterpack.category.entity.Category;
import com.starterpack.category.repository.CategoryRepository;
import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.feed.dto.FeedCreateRequestDto;
import com.starterpack.feed.dto.FeedLikeResponseDto;
import com.starterpack.feed.dto.FeedUpdateRequestDto;
import com.starterpack.feed.dto.ProductTagRequestDto;
import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.entity.FeedLike;
import com.starterpack.feed.entity.FeedProduct;
import com.starterpack.feed.repository.FeedLikeRepository;
import com.starterpack.feed.repository.FeedRepository;
import com.starterpack.feed.specification.FeedSpecification;
import com.starterpack.member.entity.Member;
import com.starterpack.product.entity.Product;
import com.starterpack.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
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
    private final ProductRepository productRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final EntityManager entityManager;

    @Transactional
    public Feed addFeed(
            Member member,
            FeedCreateRequestDto createDto) {
        Category category = getCategory(createDto.categoryId());

        Feed feed = Feed.builder()
                .user(member)
                .description(createDto.description())
                .imageUrl(createDto.imageUrl())
                .feedType(createDto.feedType())
                .category(category)
                .build();

        if (createDto.isInfoFeedWithProducts()) {
            createDto.products().forEach(productDto -> addProductToFeed(feed, productDto));
        }

        return feedRepository.save(feed);
    }

    @Transactional(readOnly = true)
    public Feed getFeed(Long feedId) {
        return getFeedByIdWithDetails(feedId);
    }

    @Transactional(readOnly = true)
    public Page<Feed> getAllFeeds(Pageable pageable) {
        return feedRepository.findAll(pageable);
    }

    @Transactional
    public Feed updateFeed(
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

        return feed;
    }

    @Transactional
    public void deleteFeed(Long feedId, Member member) {
        Feed feed = getFeedByIdWithDetails(feedId);

        feed.validateOwner(member);

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

    private void addProductToFeed(Feed feed, ProductTagRequestDto productDto) {
        Product product = new Product(
                productDto.name(),
                "link",
                "productType",
                productDto.imageUrl(),
                0,
                feed.getCategory()
        );

        productRepository.save(product);

        FeedProduct feedProduct = FeedProduct.builder()
                .feed(feed)
                .product(product)
                .description(productDto.description())
                .build();

        feed.getFeedProducts().add(feedProduct);
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

    private Feed getFeedByIdWithDetails(Long feedId) {
        return feedRepository.findByIdWithDetails(feedId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEED_NOT_FOUND));
    }

    private Feed getFeedById(Long feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEED_NOT_FOUND));
    }
}
