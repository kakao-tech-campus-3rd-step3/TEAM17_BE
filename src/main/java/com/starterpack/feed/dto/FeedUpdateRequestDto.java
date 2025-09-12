package com.starterpack.feed.dto;

public record FeedUpdateRequestDto(
        String description,
        String imageUrl,
        Long categoryId
) {
}
