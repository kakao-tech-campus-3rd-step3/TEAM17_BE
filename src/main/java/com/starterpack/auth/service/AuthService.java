package com.starterpack.auth.service;

import com.starterpack.auth.dto.LocalLoginRequestDto;
import com.starterpack.auth.dto.TokenResponseDto;
import com.starterpack.auth.jwt.JwtTokenUtil;
import com.starterpack.exception.BusinessException;
import com.starterpack.auth.dto.LocalSignUpRequestDto;
import com.starterpack.member.dto.MemberCreationRequestDto;
import com.starterpack.member.dto.MemberResponseDto;
import com.starterpack.member.entity.Member;
import com.starterpack.member.entity.Member.Provider;
import com.starterpack.member.repository.MemberRepository;
import com.starterpack.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.starterpack.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;

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
                .provider(Provider.EMAIL)
                .providerId(null)
                .birthDate(requestDto.birthDate())
                .gender(requestDto.gender())
                .phoneNumber(requestDto.phoneNumber())
                .build();

        return memberService.addMember(creationRequest);
    }

    /**
     * 로컬 로그인 처리
     * @param requestDto 이메일, 비밀번호
     * @return JWT 토큰 정보
     */
    @Transactional
    public TokenResponseDto localLogin(LocalLoginRequestDto requestDto) {

        // 이메일로 회원 조회
        Member member = memberService.findByEmail(requestDto.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 계정 유형 확인 (자체 로그인 유저만 허용)
        if (member.getProvider() != Provider.EMAIL) {
            throw new BusinessException(ErrorCode.INVALID_LOGIN_PROVIDER);
        }

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 모든 검증 통과 -> JWT 액세스, 리프레쉬 토큰 생성
        String accessToken = jwtTokenUtil.createAccessToken(member.getEmail(), member.getRole());
        String refreshToken = jwtTokenUtil.createRefreshToken(member.getEmail());

        // 리프레쉬 토큰 DB에 저장
        memberService.updateRefreshToken(member.getUserId(), refreshToken);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    // 액세스 토큰 재발급 로직
    @Transactional
    public String reissueAccessToken(String refreshToken) {
        // 리프레쉬 토큰 검증
        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtTokenUtil.getEmail(refreshToken);
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!refreshToken.equals(member.getRefreshToken())) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        return jwtTokenUtil.createAccessToken(member.getEmail(), member.getRole());
    }

    @Transactional
    public void logout(Member member) {
        // DB에서 리프레쉬 토큰을 삭제하여 무효화
        memberService.updateRefreshToken(member.getUserId(), null);
    }
}
