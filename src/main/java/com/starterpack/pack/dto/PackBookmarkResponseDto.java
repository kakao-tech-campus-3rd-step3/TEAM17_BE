package com.starterpack.pack.dto;

public record PackBookmarkResponseDto(
        Boolean isBookmarked
) {
    private static final PackBookmarkResponseDto BOOKMARKED = new PackBookmarkResponseDto(true);
    private static final PackBookmarkResponseDto UNBOOKMARKED = new PackBookmarkResponseDto(false);

    public static PackBookmarkResponseDto bookmarked() {
        return BOOKMARKED;
    }

    public static PackBookmarkResponseDto unbookmarked() {
        return UNBOOKMARKED;
    }
}
