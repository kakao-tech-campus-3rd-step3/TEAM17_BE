package com.starterpack.feed.dto;

public record FeedLikeResponseDto(
        Long likeCount,
        Boolean isLiked
) {
    public static FeedLikeResponseDto of(Long likeCount, Boolean isLiked) {
        return new FeedLikeResponseDto(likeCount, isLiked);
    }
}
