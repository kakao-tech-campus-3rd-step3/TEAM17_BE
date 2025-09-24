package com.starterpack.member.dto;

import com.starterpack.member.entity.Member;

import java.time.LocalDateTime;

public record MemberResponseDto(
        Long userId,
        String email,
        String name,
        String nickname,
        Member.Provider provider,
        String providerId,
        String profileImageUrl,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // Member 엔티티 → DTO 변환 생성자
    public MemberResponseDto(Member member) {
        this(
                member.getUserId(),
                member.getEmail(),
                member.getName(),
                member.getNickname(),
                member.getProvider(),
                member.getProviderId(),
                member.getProfileImageUrl(),
                member.getIsActive(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
