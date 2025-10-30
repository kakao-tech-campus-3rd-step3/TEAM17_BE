package com.starterpack.auth.service;

import com.starterpack.auth.dto.KakaoTokenResponseDto;
import com.starterpack.auth.dto.KakaoUserInfoResponseDto;
import com.starterpack.auth.dto.LocalLoginRequestDto;
import com.starterpack.auth.dto.TokenResponseDto;
import com.starterpack.auth.jwt.JwtTokenUtil;
import com.starterpack.auth.kakao.KakaoApiClient;
import com.starterpack.exception.BusinessException;
import com.starterpack.auth.dto.LocalSignUpRequestDto;
import com.starterpack.exception.KakaoAuthException;
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
    private final KakaoApiClient kakaoApiClient;

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

    /**
     * 카카오 로그인 및 자동 회원가입
     */
    public TokenResponseDto kakaoLogin(String code) {
        //  Kakao Api Client를 통해 액세스 토큰 및 사용자 정보 조회
        KakaoTokenResponseDto kakaoToken = kakaoApiClient.fetchAccessToken(code);
        if (kakaoToken == null) {
            throw new KakaoAuthException("Kakao token response was empty");
        }
        KakaoUserInfoResponseDto userInfo = kakaoApiClient.fetchUserInfo(kakaoToken.accessToken());
        if (userInfo == null) {
            throw new KakaoAuthException("사용자 정보 조회 실패: 응답 본문이 비어있음");
        }

        // userInfo를 토대로 MemberCreationRequestDto 생성
        MemberCreationRequestDto creationRequest = MemberCreationRequestDto.fromKakao(userInfo);

        return processKakaoLoginTransaction(creationRequest);
    }

    /**
     * 카카오 로그인 DB 작업 함수
     */
    @Transactional
    public TokenResponseDto processKakaoLoginTransaction(MemberCreationRequestDto creationRequest) {
        // providerId와 provider를 통해 존재하는 멤버인지 확인하고 없으면 새롭게 생성
        Member member = memberRepository.findByProviderAndProviderId(Provider.KAKAO, creationRequest.providerId())
                .orElseGet(() -> {
                    MemberResponseDto responseDto = memberService.addMember(creationRequest);

                    return memberRepository.findById(responseDto.userId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND, "신규 회원 생성 후 조회에 실패했습니다."));
                });

        // 토큰 발급 및 리프레쉬 토큰 저장
        String accessToken = jwtTokenUtil.createAccessToken(member.getEmail(), member.getRole());
        String refreshToken = jwtTokenUtil.createRefreshToken(member.getEmail());

        member.updateRefreshToken(refreshToken);

        return new TokenResponseDto(accessToken, refreshToken);
    }
}
