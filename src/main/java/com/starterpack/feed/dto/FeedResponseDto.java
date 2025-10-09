package com.starterpack.feed.dto;

import com.starterpack.feed.entity.Feed;
import com.starterpack.hashtag.dto.HashtagResponseDto;
import java.time.LocalDateTime;
import java.util.List;

public record FeedResponseDto(
        Long feedId,
        AuthorResponseDto author,
        String description,
        String imageUrl,
        CategoryResponseDto category,
        List<HashtagResponseDto> hashtags,
        Long likeCount,
        Long bookmarkCount,
        Long commentCount,
        FeedStatusResponseDto feedStatus,
        LocalDateTime createdAt
) {
    public FeedResponseDto(Feed feed, FeedStatusResponseDto feedStatus) {
        this(
                feed.getId(),
                AuthorResponseDto.from(feed.getUser()),
                feed.getDescription(),
                feed.getImageUrl(),
                CategoryResponseDto.from(feed.getCategory()),
                feed.getHashtags().stream()
                                .map(HashtagResponseDto::from)
                                .toList(),
                feed.getLikeCount(),
                feed.getBookmarkCount(),
                feed.getCommentCount(),
                feedStatus,
                feed.getCreatedAt()
        );
    }

    public static FeedResponseDto forAnonymous(Feed feed) {
        return new FeedResponseDto(feed, FeedStatusResponseDto.anonymousStatus());
    }

    public static FeedResponseDto forMember(Feed feed, FeedStatusResponseDto feedStatus) {
        return new FeedResponseDto(feed, feedStatus);
    }
}
