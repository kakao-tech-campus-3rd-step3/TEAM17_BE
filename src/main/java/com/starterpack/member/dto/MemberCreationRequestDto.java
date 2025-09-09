package com.starterpack.member.dto;

import com.starterpack.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
// 멤버 생성을 위한 내부 DTO
public class MemberCreationRequestDto {
    private final String email;
    private final String encodedPassword;
    private final String name;
    private final Member.Provider provider;
    private final String providerId;
    private final String profileImageUrl;

    @Builder
    public MemberCreationRequestDto(String email, String encodedPassword, String name, Member.Provider provider, String providerId, String profileImageUrl) {
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImageUrl = profileImageUrl;
    }
}
