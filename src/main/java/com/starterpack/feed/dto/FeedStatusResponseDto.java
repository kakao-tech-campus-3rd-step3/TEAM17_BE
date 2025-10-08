package com.starterpack.feed.dto;

public record FeedStatusResponseDto(
        Boolean isLiked,
        Boolean isBookmarked
) {
    private static final FeedStatusResponseDto ANONYMOUS_STATUS = new FeedStatusResponseDto(false, false);

    public static FeedStatusResponseDto anonymousStatus() {
        return ANONYMOUS_STATUS;
    }

    public static FeedStatusResponseDto of(Boolean isLiked, Boolean isBookmarked) {
        return new FeedStatusResponseDto(isLiked, isBookmarked);
    }
}
