package com.starterpack.pack.dto;

import com.starterpack.pack.entity.Pack;

public record PackRecommendDto(
        Long id,
        String name,
        Integer price,
        String mainImageUrl,
        Integer likeCount,
        Integer bookmarkCount,
        double score
) {
    public static PackRecommendDto from(Pack pack, double score) {
        return new PackRecommendDto(
                pack.getId(),
                pack.getName(),
                pack.getPrice(),
                pack.getMainImageUrl(),
                pack.getPackLikeCount(),
                pack.getPackBookmarkCount(),
                score
        );
    }
}
