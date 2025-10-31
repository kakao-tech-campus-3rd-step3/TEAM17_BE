package com.starterpack.member.dto;

import com.starterpack.pack.entity.Pack;

// 마이페이지에서 사용할 간단한 팩 정보
public record MyPagePackDto(
        Long packId,
        String name,
        String mainImageUrl
) {
    public static MyPagePackDto from(Pack pack) {
        return new MyPagePackDto(
                pack.getId(),
                pack.getName(),
                pack.getMainImageUrl()
        );
    }
}

