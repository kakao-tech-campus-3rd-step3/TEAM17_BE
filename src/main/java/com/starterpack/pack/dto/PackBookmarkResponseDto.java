package com.starterpack.pack.dto;

public record PackBookmarkResponseDto(
        Boolean isBookmarked
) {
    public static PackBookmarkResponseDto of(Boolean isBookmarked) {
        return new PackBookmarkResponseDto(isBookmarked);
    }
}
