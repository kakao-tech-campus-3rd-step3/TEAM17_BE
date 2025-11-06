package com.starterpack.pack.dto;

import com.starterpack.hashtag.dto.HashtagResponseDto;
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
        List<HashtagResponseDto> hashtags,
        Integer likeCount,
        Integer bookmarkCount,
        Integer commentCount,
        String authorNickname,
        Long memberId,
        InteractionStatusResponseDto interactionStatusResponseDto
) {
    public PackDetailResponseDto(Pack pack, InteractionStatusResponseDto status) {
        this(
                pack.getId(),
                pack.getName(),
                pack.getPrice(),
                pack.getDescription(),
                pack.getMainImageUrl(),
                pack.getCategory().getId(),
                pack.getCategory().getName(),
                pack.getItems().stream() 
                        .map(PackItemDto::from)
                        .toList(),
                pack.getHashtags().stream()
                        .map(HashtagResponseDto::from)
                        .toList(),
                pack.getPackLikeCount(),
                pack.getPackBookmarkCount(),
                pack.getPackCommentCount(),
                pack.getMember().getNickname(),
                pack.getMember().getUserId(),
                status
        );
    }

    public static PackDetailResponseDto forAnonymous(Pack pack) {
        return new PackDetailResponseDto(pack, InteractionStatusResponseDto.anonymousStatus());
    }

    public static PackDetailResponseDto forMember(Pack pack, InteractionStatusResponseDto status) {
        return new PackDetailResponseDto(pack, status);
    }
}
