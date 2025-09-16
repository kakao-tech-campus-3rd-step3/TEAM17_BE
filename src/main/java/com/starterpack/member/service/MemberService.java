package com.starterpack.member.service;

import com.starterpack.member.dto.MemberCreationRequestDto;
import com.starterpack.member.dto.MemberResponseDto;
import com.starterpack.member.dto.MemberUpdateRequestDto;
import com.starterpack.member.entity.Member;
import com.starterpack.member.entity.Role;
import com.starterpack.member.repository.MemberRepository;
import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 모든 멤버 조회
    public List<MemberResponseDto> findAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberResponseDto::new)
                .collect(Collectors.toList());
    }

    // ID로 멤버 조회
    public MemberResponseDto findMemberById(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberResponseDto(member);
    }

    // 이메일로 멤버 조회 (내부용)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    // 이메일로 멤버 조회 (컨트롤러 용)
    public MemberResponseDto findMemberByEmail(String email) {
        Member member = this.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberResponseDto(member);
    }

    // 프로바이더와 프로바이더 ID로 멤버 조회 (소셜 로그인용)
    public MemberResponseDto findMemberByProviderAndProviderId(Member.Provider provider, String providerId) {
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberResponseDto(member);
    }

    @Transactional
    public MemberResponseDto addMember(MemberCreationRequestDto request) {
        // 이메일 중복 확인
        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.MEMBER_EMAIL_DUPLICATED);
        }

        // 일반 사용자 생성
        Member member = Member.createUser(
                request.email(),
                request.encodedPassword(),
                request.name(),
                request.provider(),
                request.providerId()
        );

        if (request.profileImageUrl() != null) {
            member.setProfileImageUrl(request.profileImageUrl());
        }

        return new MemberResponseDto(memberRepository.save(member));
    }

    // 멤버 정보 수정
    @Transactional
    public MemberResponseDto updateMember(Long userId, MemberUpdateRequestDto requestDto) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 이메일 변경 시 중복 확인
        if (requestDto.email() != null && !requestDto.email().equals(member.getEmail())) {
            if (memberRepository.existsByEmail(requestDto.email())) {
                throw new BusinessException(ErrorCode.MEMBER_EMAIL_DUPLICATED);
            }
            member.setEmail(requestDto.email());
        }

        // 비밀번호 암호화해서 저장
        if (requestDto.password() != null && !requestDto.password().isBlank()) {
            member.setPassword(passwordEncoder.encode(requestDto.password()));
        }

        if (requestDto.name() != null) {
            member.setName(requestDto.name());
        }

        if (requestDto.profileImageUrl() != null) {
            member.setProfileImageUrl(requestDto.profileImageUrl());
        }

        Member updatedMember = memberRepository.save(member);
        return new MemberResponseDto(updatedMember);
    }

    // 멤버 삭제 (소프트 삭제)
    @Transactional
    public void deleteMember(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        
        member.setIsActive(false);
        memberRepository.save(member);
    }

    // 멤버 완전 삭제
    @Transactional
    public void deleteMemberPermanently(Long userId) {
        if (!memberRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        memberRepository.deleteById(userId);
    }

    // 멤버 활성화 상태 변경
    @Transactional
    public MemberResponseDto updateMemberActiveStatus(Long userId, Boolean isActive) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        
        member.setIsActive(isActive);
        Member updatedMember = memberRepository.save(member);
        return new MemberResponseDto(updatedMember);
    }
}
