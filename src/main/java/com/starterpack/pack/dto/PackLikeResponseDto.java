package com.starterpack.pack.dto;

public record PackLikeResponseDto(
        Integer likeCount,
        Boolean isLiked
) {
    public static PackLikeResponseDto of(Integer likeCount, Boolean isLiked) {
        return new PackLikeResponseDto(likeCount, isLiked);
    }

    public static PackLikeResponseDto liked(Integer likeCount) {
        return new PackLikeResponseDto(likeCount, true);
    }

    public static PackLikeResponseDto unliked(Integer likeCount) {
        return new PackLikeResponseDto(likeCount, false);
    }
}
