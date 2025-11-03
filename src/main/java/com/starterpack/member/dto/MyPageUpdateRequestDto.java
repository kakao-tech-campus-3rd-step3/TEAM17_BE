package com.starterpack.member.dto;

import jakarta.validation.constraints.Size;

// 마이페이지 정보 수정 요청 DTO
public record MyPageUpdateRequestDto(
        @Size(max = 500, message = "프로필 이미지 URL은 500자를 초과할 수 없습니다")
        String profileImageUrl,

        @Size(max = 50, message = "닉네임은 50자를 초과할 수 없습니다")
        String nickname,

        @Size(max = 200, message = "취미는 200자를 초과할 수 없습니다")
        String hobby,

        @Size(max = 500, message = "소개는 500자를 초과할 수 없습니다")
        String bio
) {
}

