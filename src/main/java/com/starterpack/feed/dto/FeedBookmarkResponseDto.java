package com.starterpack.feed.dto;

public record FeedBookmarkResponseDto(
        Boolean isBookmarked
) {
    public static FeedBookmarkResponseDto of(Boolean isBookmarked) {
        return new FeedBookmarkResponseDto(isBookmarked);
    }
}
