package com.starterpack.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResponseDto(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            Profile profile,
            String email,
            String birthyear, // YYYY 형식
            String birthday, // MMDD 형식
            String gender, // male, female
            @JsonProperty("phone_number") String phoneNumber // +82 00-0000-0000 형식
    ) {}

    public record Profile(
            String nickname
    ) {}
}
