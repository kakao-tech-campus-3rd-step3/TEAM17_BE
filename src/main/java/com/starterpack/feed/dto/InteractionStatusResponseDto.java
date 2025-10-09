package com.starterpack.feed.dto;

public record InteractionStatusResponseDto(
        Boolean isLiked,
        Boolean isBookmarked
) {
    private static final InteractionStatusResponseDto ANONYMOUS_STATUS = new InteractionStatusResponseDto(false, false);

    public static InteractionStatusResponseDto anonymousStatus() {
        return ANONYMOUS_STATUS;
    }

    public static InteractionStatusResponseDto of(Boolean isLiked, Boolean isBookmarked) {
        return new InteractionStatusResponseDto(isLiked, isBookmarked);
    }
}
