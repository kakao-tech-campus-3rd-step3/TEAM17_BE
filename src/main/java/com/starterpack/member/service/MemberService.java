package com.starterpack.member.service;

import com.starterpack.member.dto.MemberCreateRequestDto;
import com.starterpack.member.dto.MemberResponseDto;
import com.starterpack.member.dto.MemberUpdateRequestDto;
import com.starterpack.member.entity.Member;
import com.starterpack.member.repository.MemberRepository;
import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

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

    // 이메일로 멤버 조회
    public MemberResponseDto findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberResponseDto(member);
    }

    // 프로바이더와 프로바이더 ID로 멤버 조회 (소셜 로그인용)
    public MemberResponseDto findMemberByProviderAndProviderId(Member.Provider provider, String providerId) {
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberResponseDto(member);
    }

    // 멤버 생성
    @Transactional
    public MemberResponseDto addMember(MemberCreateRequestDto requestDto) {
        // 이메일 중복 확인
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new BusinessException(ErrorCode.MEMBER_EMAIL_DUPLICATED);
        }

        // 소셜 로그인인 경우 프로바이더 ID 중복 확인
        if (requestDto.getProvider() != Member.Provider.EMAIL && 
            memberRepository.findByProviderAndProviderId(requestDto.getProvider(), requestDto.getProviderId()).isPresent()) {
            throw new BusinessException(ErrorCode.MEMBER_PROVIDER_ID_DUPLICATED);
        }

        Member member = new Member(
                requestDto.getEmail(),
                requestDto.getPassword(),
                requestDto.getName(),
                requestDto.getProvider(),
                requestDto.getProviderId()
        );
        
        if (requestDto.getProfileImageUrl() != null) {
            member.setProfileImageUrl(requestDto.getProfileImageUrl());
        }

        Member savedMember = memberRepository.save(member);
        return new MemberResponseDto(savedMember);
    }

    // 멤버 정보 수정
    @Transactional
    public MemberResponseDto updateMember(Long userId, MemberUpdateRequestDto requestDto) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 이메일 변경 시 중복 확인
        if (requestDto.getEmail() != null && !requestDto.getEmail().equals(member.getEmail())) {
            if (memberRepository.existsByEmail(requestDto.getEmail())) {
                throw new BusinessException(ErrorCode.MEMBER_EMAIL_DUPLICATED);
            }
            member.setEmail(requestDto.getEmail());
        }

        if (requestDto.getPassword() != null) {
            member.setPassword(requestDto.getPassword());
        }

        if (requestDto.getName() != null) {
            member.setName(requestDto.getName());
        }

        if (requestDto.getProfileImageUrl() != null) {
            member.setProfileImageUrl(requestDto.getProfileImageUrl());
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
