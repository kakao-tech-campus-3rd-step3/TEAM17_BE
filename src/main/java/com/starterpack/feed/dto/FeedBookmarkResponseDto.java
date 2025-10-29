package com.starterpack.feed.dto;

public record FeedBookmarkResponseDto(
        Boolean isBookmarked
) {
    private static final FeedBookmarkResponseDto BOOKMARED = new FeedBookmarkResponseDto(true);
    private static final FeedBookmarkResponseDto UNBOOKMARED = new FeedBookmarkResponseDto(false);

    public static FeedBookmarkResponseDto bookmared() {
        return BOOKMARED;
    }

    public static FeedBookmarkResponseDto unbookmared() {
        return UNBOOKMARED;
    }
}
