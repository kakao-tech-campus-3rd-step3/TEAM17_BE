package com.starterpack.feed.dto;

public record FeedBookmarkResponseDto(
        Boolean isBookmarked
) {
    private static final FeedBookmarkResponseDto BOOKMARKED = new FeedBookmarkResponseDto(true);
    private static final FeedBookmarkResponseDto UNBOOKMARKED = new FeedBookmarkResponseDto(false);

    public static FeedBookmarkResponseDto bookmared() {
        return BOOKMARKED;
    }

    public static FeedBookmarkResponseDto unbookmared() {
        return UNBOOKMARKED;
    }
}
