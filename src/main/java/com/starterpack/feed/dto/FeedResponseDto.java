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
        FeedStatsResponseDto stats,
        InteractionStatusResponseDto interactionStatus,
        LocalDateTime createdAt
) {
    public FeedResponseDto(Feed feed, InteractionStatusResponseDto interactionStatus) {
        this(
                feed.getId(),
                AuthorResponseDto.from(feed.getUser()),
                feed.getDescription(),
                feed.getImageUrl(),
                CategoryResponseDto.from(feed.getCategory()),
                feed.getHashtags().stream()
                                .map(HashtagResponseDto::from)
                                .toList(),
                FeedStatsResponseDto.from(feed),
                interactionStatus,
                feed.getCreatedAt()
        );
    }

    public static FeedResponseDto forAnonymous(Feed feed) {
        return new FeedResponseDto(feed, InteractionStatusResponseDto.anonymousStatus());
    }

    public static FeedResponseDto forMember(Feed feed, InteractionStatusResponseDto interactionStatus) {
        return new FeedResponseDto(feed, interactionStatus);
    }
}
