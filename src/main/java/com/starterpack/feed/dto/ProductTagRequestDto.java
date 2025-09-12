package com.starterpack.feed.dto;

public record ProductTagRequestDto(
        String name,
        String imageUrl,
        String description
) {
}
