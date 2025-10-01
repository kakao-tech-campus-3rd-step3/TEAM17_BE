package com.starterpack.auth.dto;

/**
 * 로그인 성공 시 JWT 토큰들을 담아 반환할 응답 DTO
 * @param grantType 인증 타입 ("Bearer")
 * @param accessToken 발급된 JWT 액세스 토큰
 * @param refreshToken 발급된 JWT 리프레시 토큰
 */
public record TokenResponseDto(
        String grantType,
        String accessToken,
        String refreshToken
) {
    public TokenResponseDto(String accessToken, String refreshToken) {
        this("Bearer", accessToken, refreshToken);
    }
}
