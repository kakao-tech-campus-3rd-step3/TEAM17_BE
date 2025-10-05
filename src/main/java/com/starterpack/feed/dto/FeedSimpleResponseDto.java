package com.starterpack.feed.dto;

import com.starterpack.feed.entity.Feed;
import java.time.LocalDateTime;

public record FeedSimpleResponseDto(
        Long feedId,
        AuthorResponseDto author,
        String description,
        String imageUrl,
        CategoryResponseDto category,
        Long likeCount,
        Long bookmarkCount,
        Long commentCount,
        FeedStatusResponseDto feedStatus,
        LocalDateTime createdAt
) {
    public FeedSimpleResponseDto(Feed feed, FeedStatusResponseDto feedStatus) {
        this(
                feed.getId(),
                AuthorResponseDto.from(feed.getUser()),
                feed.getDescription(),
                feed.getImageUrl(),
                CategoryResponseDto.from(feed.getCategory()),
                feed.getLikeCount(),
                feed.getBookmarkCount(),
                feed.getCommentCount(),
                feedStatus,
                feed.getCreatedAt()
        );
    }

    public static FeedSimpleResponseDto forAnonymous(Feed feed) {
        return new FeedSimpleResponseDto(feed, FeedStatusResponseDto.anonymousStatus());
    }

    public static FeedSimpleResponseDto forMember(Feed feed, FeedStatusResponseDto feedStatus) {
        return new FeedSimpleResponseDto(feed, feedStatus);
    }
}
