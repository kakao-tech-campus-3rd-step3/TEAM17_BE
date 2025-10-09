package com.starterpack.feed.dto;

import java.util.List;

public record FeedCreateRequestDto(
    String description,
    String imageUrl,
    Long categoryId,
    List<String> hashtagNames
) {
}
