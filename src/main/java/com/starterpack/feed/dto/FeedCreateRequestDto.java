package com.starterpack.feed.dto;

public record FeedCreateRequestDto(
    String description,
    String imageUrl,
    Long categoryId
) {
}
