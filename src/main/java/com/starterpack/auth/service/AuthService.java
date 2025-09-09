package com.starterpack.auth.service;

import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.member.dto.MemberCreateRequestDto;
import com.starterpack.member.dto.MemberResponseDto;
import com.starterpack.member.entity.Member;
import com.starterpack.member.entity.Member.Provider;
import com.starterpack.member.repository.MemberRepository;
import com.starterpack.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 자체 회원가입 기능
    @Transactional
    public MemberResponseDto localSignUp(MemberCreateRequestDto requestDto) {
        // 이메일 중복 확인
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new BusinessException(ErrorCode.MEMBER_EMAIL_DUPLICATED);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Member member = new Member(
                requestDto.getEmail(),
                encodedPassword, // 암호화된 비밀번호로 DB에 저장
                requestDto.getName(),
                Provider.EMAIL,
                null // providerId는 로컬 회원가입에서 사용하지 않음
        );

        if (requestDto.getProfileImageUrl() != null) {
            member.setProfileImageUrl(requestDto.getProfileImageUrl());
        }

        Member savedMember = memberRepository.save(member);
        return new MemberResponseDto(savedMember);
    }

}
