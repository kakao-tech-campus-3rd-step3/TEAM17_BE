package com.starterpack.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record MemberUpdateRequestDto(

        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
        String password,

        @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
        String name,

        String profileImageUrl
) {}
