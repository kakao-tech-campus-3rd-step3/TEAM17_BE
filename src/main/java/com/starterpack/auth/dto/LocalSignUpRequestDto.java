package com.starterpack.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 로컬 회원가입 요청을 받기 위한 DTO (provider와 providerId가 필요 없음)
public record LocalSignUpRequestDto(

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
        String password,

        @NotBlank(message = "이름은 필수입니다")
        @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
        String name,

        String profileImageUrl
) {}
