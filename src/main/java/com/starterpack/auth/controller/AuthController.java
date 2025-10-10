package com.starterpack.auth.controller;

import com.starterpack.auth.dto.LocalLoginRequestDto;
import com.starterpack.auth.dto.TokenResponseDto;
import com.starterpack.auth.login.Login;
import com.starterpack.auth.service.AuthService;
import com.starterpack.auth.dto.LocalSignUpRequestDto;
import com.starterpack.member.dto.MemberResponseDto;
import com.starterpack.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로컬 회원가입 API
     */
    @PostMapping("/signup")
    @Operation(summary = "자체 회원가입", description = "이메일과 비밀번호를 사용하여 신규 회원을 등록합니다.")
    public ResponseEntity<MemberResponseDto> localSignUp(
            @Valid @RequestBody LocalSignUpRequestDto requestDto
    ) {
        MemberResponseDto responseDto = authService.localSignUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    
    /**
     * 로컬 로그인 API
     */
    @PostMapping("/login")
    @Operation(summary = "로컬 로그인", description = "이메일과 비밀번호로 인증 후, Access/Refresh 토큰을 HttpOnly 쿠키로 발급합니다.")
    public ResponseEntity<Void> localLogin(
            @Valid @RequestBody LocalLoginRequestDto requestDto,
            HttpServletResponse  response
    ) {
        TokenResponseDto tokenResponseDto = authService.localLogin(requestDto);

        ResponseCookie accessTokenCookie = ResponseCookie.from("jwt_token", tokenResponseDto.accessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 30) // 30분
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        ResponseCookie refreshTokenCookie  = ResponseCookie.from("refresh_token", tokenResponseDto.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 14) // 14일
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok().build();

    }

    @PostMapping("/refresh")
    @Operation(summary = "Access Token 재발급", description = "유효한 Refresh Token 쿠키를 사용하여 새로운 Access Token을 발급받습니다.")
    public ResponseEntity<Void> reissueAccessToken(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response
    ) {
        String newAccessToken = authService.reissueAccessToken(refreshToken);

        ResponseCookie accessTokenCookie = ResponseCookie.from("jwt_token", newAccessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 30) // 30분
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "서버의 Refresh Token을 무효화하고 클라이언트의 토큰 쿠키를 삭제합니다.")
    @SecurityRequirement(name = "CookieAuthentication")
    public ResponseEntity<Void> logout(
            @Login Member member,
            HttpServletResponse response
    ) {
        authService.logout(member);

        expireCookie(response, "jwt_token");
        expireCookie(response, "refresh_token");

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "현재 로그인된 사용자 정보 조회", description = "인증된 사용자의 상세 정보를 반환합니다.")
    @SecurityRequirement(name = "CookieAuthentication")
    public ResponseEntity<MemberResponseDto> getCurrentMember(@Login Member member) {
        MemberResponseDto responseDto = new MemberResponseDto(member);

        return ResponseEntity.ok(responseDto);
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .maxAge(0)
                .path("/")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @GetMapping("/kakao/callback")
    @Operation(summary = "[내부 API] 카카오 로그인 콜백", description = "카카오 서버로부터 인가 코드를 받아 로그인/회원가입을 처리하는 내부용 API")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) throws IOException {

        TokenResponseDto tokenResponseDto = authService.kakaoLogin(code);

        ResponseCookie accessTokenCookie = ResponseCookie.from("jwt_token", tokenResponseDto.accessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 30) // 30분
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        ResponseCookie refreshTokenCookie  = ResponseCookie.from("refresh_token", tokenResponseDto.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 14) // 14일
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        response.sendRedirect("https://team-17-fe-theta.vercel.app");
    }

    // CSRF 토큰을 쿠키로 발급
    @GetMapping("/csrf-token")
    @Operation(summary = "CSRF 토큰 발급", description = "클라이언트에게 CSRF 토큰을 쿠키로 발급합니다.")
    public ResponseEntity<String> getCsrfToken(CsrfToken csrfToken) {
        // 토큰 값을 응답으로 반환해서 디버깅
        String token = csrfToken.getToken();
        return ResponseEntity.ok(token);
    }
}
