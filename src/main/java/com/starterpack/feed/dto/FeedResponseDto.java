package com.starterpack.feed.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.entity.FeedType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record FeedResponseDto(
        Long feedId,
        AuthorResponseDto author,
        String description,
        String imageUrl,
        FeedType feedType,
        CategoryResponseDto category,
        Long likeCount,
        LocalDateTime createdAt,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<TaggedProductResponseDto> products
) {
    public static FeedResponseDto from(Feed feed) {
        CategoryResponseDto category = (feed.getCategory() != null) ? CategoryResponseDto.from(feed.getCategory()) : null;

        return new FeedResponseDto(
                feed.getId(),
                AuthorResponseDto.from(feed.getUser()),
                feed.getDescription(),
                feed.getImageUrl(),
                feed.getFeedType(),
                category,
                feed.getLikeCount(),
                feed.getCreatedAt(),
                feed.getFeedProducts().stream()
                        .map(TaggedProductResponseDto::from)
                        .collect(Collectors.toList())
        );
    }
}
