package com.starterpack.feed.dto;

import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.entity.FeedType;
import java.time.LocalDateTime;

public record FeedSimpleResponseDto(
        Long feedId,
        AuthorResponseDto author,
        String description,
        String imageUrl,
        FeedType feedType,
        CategoryResponseDto category,
        Long likeCount,
        Long bookmarkCount,
        LocalDateTime createdAt
) {
    public static FeedSimpleResponseDto from(Feed feed) {
        CategoryResponseDto category = (feed.getCategory() != null)
                ? CategoryResponseDto.from(feed.getCategory())
                : null;

        return new FeedSimpleResponseDto(
                feed.getId(),
                AuthorResponseDto.from(feed.getUser()),
                feed.getDescription(),
                feed.getImageUrl(),
                feed.getFeedType(),
                category,
                feed.getLikeCount(),
                feed.getBookmarkCount(),
                feed.getCreatedAt()
        );
    }
}
