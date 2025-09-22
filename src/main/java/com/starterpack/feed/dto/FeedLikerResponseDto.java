package com.starterpack.feed.dto;

import com.starterpack.member.entity.Member;

public record FeedLikerResponseDto(
        Long userId,
        String name,
        String profileImageUrl
) {
    public static FeedLikerResponseDto from(Member member){
        return new FeedLikerResponseDto(
                member.getUserId(),
                member.getName(),
                member.getProfileImageUrl()
        );
    }
}
