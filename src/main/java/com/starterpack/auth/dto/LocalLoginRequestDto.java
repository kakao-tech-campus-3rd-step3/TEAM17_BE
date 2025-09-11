package com.starterpack.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 로컬 로그인을 위한 요청 DTO
 * @param email 사용자 이메일
 * @param password 사용자 비밀번호
 */
public record LocalLoginRequestDto(
        @NotBlank(message = "이메일은 필수 항목입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        String password
) {
}
