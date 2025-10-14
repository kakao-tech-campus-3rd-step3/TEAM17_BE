package com.starterpack.pack.dto;

import com.starterpack.pack.entity.Pack;
import java.util.List;

public record PackResponseDto(
        Long id,
        String name,
        Integer price,
        String description,
        String mainImageUrl,
        List<PackItemDto> items,
        Integer likeCount,
        Integer bookmarkCount,
        Integer commentCount,
        String authorNickname,
        Long memberId
) {
    public static PackResponseDto from(Pack pack) {
        List<PackItemDto> items = pack.getItems().stream()
                .map(PackItemDto::from)
                .toList();

        return new PackResponseDto(
                pack.getId(),
                pack.getName(),
                pack.getPrice(),
                pack.getDescription(),
                pack.getMainImageUrl(),
                items,
                pack.getPackLikeCount(),
                pack.getPackBookmarkCount(),
                pack.getPackCommentCount(),
                pack.getMember().getNickname(),
                pack.getMember().getUserId()
        );
    }
}
