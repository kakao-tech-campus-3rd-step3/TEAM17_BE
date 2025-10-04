package com.starterpack.member.dto;

import com.starterpack.auth.dto.KakaoUserInfoResponseDto;
import com.starterpack.member.entity.Gender;
import com.starterpack.member.entity.Member;
import com.starterpack.member.entity.Member.Provider;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

// 멤버 생성을 위한 내부 DTO
@Builder
public record MemberCreationRequestDto(
        String email,
        String encodedPassword,
        String name,
        Member.Provider provider,
        String providerId,
        String profileImageUrl,
        LocalDate birthDate,
        Gender gender,
        String phoneNumber
) {
    public static MemberCreationRequestDto fromKakao(KakaoUserInfoResponseDto userInfo) {
        KakaoUserInfoResponseDto.KakaoAccount kakaoAccount = userInfo.kakaoAccount();

        Gender gender = null;
        if (kakaoAccount.gender() != null) {
            gender = "male".equalsIgnoreCase(kakaoAccount.gender()) ? Gender.MALE : Gender.FEMALE;
        }

        LocalDate birthDate = null;
        if (kakaoAccount.birthyear() != null && kakaoAccount.birthday() != null) {
            String yyyyMMdd = kakaoAccount.birthyear() + kakaoAccount.birthday();
            birthDate = LocalDate.parse(yyyyMMdd, DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        String phoneNumber = null;
        if (kakaoAccount.phoneNumber() != null) {
            phoneNumber = kakaoAccount.phoneNumber().replace("+82 ", "0");
        }

        return MemberCreationRequestDto.builder()
                .email(kakaoAccount.email())
                .name(kakaoAccount.profile().nickname())
                .provider(Provider.KAKAO)
                .providerId(userInfo.id().toString())
                .birthDate(birthDate)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .build();
    }
}
