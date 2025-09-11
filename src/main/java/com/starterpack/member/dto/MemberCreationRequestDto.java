package com.starterpack.member.dto;

import com.starterpack.member.entity.Member;
import lombok.Builder;

// 멤버 생성을 위한 내부 DTO
@Builder
public record MemberCreationRequestDto(
        String email,
        String encodedPassword,
        String name,
        Member.Provider provider,
        String providerId,
        String profileImageUrl
) {}
