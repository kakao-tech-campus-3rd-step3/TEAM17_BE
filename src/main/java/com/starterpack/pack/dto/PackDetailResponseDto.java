package com.starterpack.pack.dto;

import com.starterpack.pack.entity.Pack;
import java.util.List;

public record PackDetailResponseDto(
        Long id,
        String name,
        Integer price,
        String description,
        String mainImageUrl,
        Long categoryId,
        String categoryName,
        List<PackItemDto> items,
        Integer likeCount,
        Integer bookmarkCount,
        Integer commentCount,
        String authorNickname,
        Long memberId
) {
    public static PackDetailResponseDto from(Pack pack) {
        List<PackItemDto> items = pack.getItems().stream()
                .map(PackItemDto::from)
                .toList();

        return new PackDetailResponseDto(
                pack.getId(),
                pack.getName(),
                pack.getPrice(),
                pack.getDescription(),
                pack.getMainImageUrl(),
                pack.getCategory().getId(),
                pack.getCategory().getName(),
                items,
                pack.getPackLikeCount(),
                pack.getPackBookmarkCount(),
                pack.getPackCommentCount(),
                pack.getMember().getNickname(),
                pack.getMember().getUserId()
        );
    }
}
