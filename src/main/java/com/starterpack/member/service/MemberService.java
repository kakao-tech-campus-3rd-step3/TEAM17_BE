package com.starterpack.member.service;

import com.starterpack.member.dto.MemberCreationRequestDto;
import com.starterpack.member.dto.MemberResponseDto;
import com.starterpack.member.dto.MemberUpdateRequestDto;
import com.starterpack.member.dto.MyPageResponseDto;
import com.starterpack.member.entity.Member;
import com.starterpack.member.repository.MemberRepository;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.repository.PackRepository;
import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.repository.FeedRepository;
import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final NicknameService nicknameService;
    private final PackRepository packRepository;
    private final FeedRepository feedRepository;

    // 모든 멤버 조회
    public List<Member> findAllMembers() {
        return memberRepository.findAll();
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

        // 랜덤 닉네임 생성
        String randomNickname = nicknameService.generateUniqueNickname();

        // 일반 사용자 생성
        Member member = Member.createUser(
                request.email(),
                request.encodedPassword(),
                request.name(),
                randomNickname,
                request.provider(),
                request.providerId(),
                request.birthDate(),
                request.gender(),
                request.phoneNumber()
        );

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

        if (requestDto.birthDate() != null) {
            member.setBirthDate(requestDto.birthDate());
        }

        if (requestDto.gender() != null) {
            member.setGender(requestDto.gender());
        }

        if (requestDto.phoneNumber() != null) {
            member.setPhoneNumber(requestDto.phoneNumber());
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

    // 리프레쉬 토큰 업데이트
    @Transactional
    public void updateRefreshToken(Long memberId, String refreshToken) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        member.updateRefreshToken(refreshToken);
    }

    // 마이페이지 조회
    public MyPageResponseDto getMyPage(Long targetUserId, Long currentUserId) {
        Member member = memberRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        List<Pack> packs = packRepository.findByMemberId(targetUserId);
        List<Feed> feeds = feedRepository.findByUserId(targetUserId, 
                org.springframework.data.domain.Pageable.unpaged()).getContent();

        boolean isMe = currentUserId != null && currentUserId.equals(targetUserId);

        return MyPageResponseDto.from(member, packs, feeds, isMe);
    }
}
