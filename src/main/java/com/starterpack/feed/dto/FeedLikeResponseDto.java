package com.starterpack.feed.dto;

public record FeedLikeResponseDto(
        Long likeCount,
        Boolean isLiked
) {
    public static FeedLikeResponseDto liked(Long likeCount) {
        return new FeedLikeResponseDto(likeCount, true);
    }

    public static FeedLikeResponseDto unliked(Long likeCount) {
        return new FeedLikeResponseDto(likeCount, false);
    }
}
