package com.starterpack.feed.dto;

import com.starterpack.member.entity.Member;

public record AuthorResponseDto(
        Long userId,
        String name,
        String profileImageUrl
) {
        public static AuthorResponseDto from(Member member){
            return new AuthorResponseDto(
                    member.getUserId(),
                    member.getNickname(),
                    member.getProfileImageUrl()
            );
        }
}
