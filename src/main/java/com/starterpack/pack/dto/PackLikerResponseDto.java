package com.starterpack.pack.dto;

import com.starterpack.member.entity.Member;

public record PackLikerResponseDto(
        Long userId,
        String name,
        String profileImageUrl
) {
    public static PackLikerResponseDto from(Member member) {
        return new PackLikerResponseDto(
                member.getUserId(),
                member.getName(),
                member.getProfileImageUrl()
        );
    }
}
