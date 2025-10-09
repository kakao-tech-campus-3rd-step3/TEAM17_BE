package com.starterpack.feed.dto;

import java.util.List;

public record FeedUpdateRequestDto(
        String description,
        String imageUrl,
        Long categoryId,
        List<String> hashtagNames
) {
}
