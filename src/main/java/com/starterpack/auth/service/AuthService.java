package com.starterpack.auth.service;

import com.starterpack.member.dto.LocalSignUpRequestDto;
import com.starterpack.member.dto.MemberCreationRequestDto;
import com.starterpack.member.dto.MemberResponseDto;
import com.starterpack.member.entity.Member;
import com.starterpack.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    /**
     * AuthService의 책임:
     * 1. '자체 회원가입'에 필요한 데이터를 가공 (비밀번호 암호화)
     * 2. '표준화된 요청 객체'로 변환
     * 3. 'MemberService'에 생성 위임
     */

    // 자체 회원가입 기능
    @Transactional
    public MemberResponseDto localSignUp(LocalSignUpRequestDto requestDto) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.password());

        // 표준화된 내부 객체로 변환
        MemberCreationRequestDto creationRequest = MemberCreationRequestDto.builder()
                .email(requestDto.email())
                .encodedPassword(encodedPassword)
                .name(requestDto.name())
                .provider(Member.Provider.EMAIL)
                .providerId(null)
                .build();

        return memberService.addMember(creationRequest);
    }

}
