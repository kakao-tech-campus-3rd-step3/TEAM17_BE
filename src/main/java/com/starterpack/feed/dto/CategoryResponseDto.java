package com.starterpack.feed.dto;

import com.starterpack.category.entity.Category;

public record CategoryResponseDto(
        Long categoryId,
        String categoryName
) {
    public static CategoryResponseDto from(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName());
    }
}
