package com.starterpack.feed.dto;

import com.starterpack.feed.entity.Feed;
import java.time.LocalDateTime;

public record FeedResponseDto(
        Long feedId,
        AuthorResponseDto author,
        String description,
        String imageUrl,
        CategoryResponseDto category,
        Long likeCount,
        Long bookmarkCount,
        LocalDateTime createdAt
) {
    public static FeedResponseDto from(Feed feed) {
        CategoryResponseDto category = (feed.getCategory() != null) ? CategoryResponseDto.from(feed.getCategory()) : null;

        return new FeedResponseDto(
                feed.getId(),
                AuthorResponseDto.from(feed.getUser()),
                feed.getDescription(),
                feed.getImageUrl(),
                category,
                feed.getLikeCount(),
                feed.getBookmarkCount(),
                feed.getCreatedAt()
        );
    }
}
