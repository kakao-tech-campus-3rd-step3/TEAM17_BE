package com.starterpack.feed.dto;

public record FeedBookmarkResponseDto(
        Boolean isBookmarked
) {
    private static final FeedBookmarkResponseDto BOOKMARKED = new FeedBookmarkResponseDto(true);
    private static final FeedBookmarkResponseDto UNBOOKMARKED = new FeedBookmarkResponseDto(false);

    public static FeedBookmarkResponseDto bookmarked() {
        return BOOKMARKED;
    }

    public static FeedBookmarkResponseDto unbookmarked() {
        return UNBOOKMARKED;
    }
}
