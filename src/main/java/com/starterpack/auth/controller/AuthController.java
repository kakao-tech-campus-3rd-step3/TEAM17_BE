package com.starterpack.auth.controller;

import com.starterpack.auth.dto.LocalLoginRequestDto;
import com.starterpack.auth.dto.TokenResponseDto;
import com.starterpack.auth.login.Login;
import com.starterpack.auth.service.AuthService;
import com.starterpack.auth.dto.LocalSignUpRequestDto;
import com.starterpack.member.dto.MemberResponseDto;
import com.starterpack.member.entity.Member;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로컬 회원가입 API
     */
    @PostMapping("/signup")
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
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        ResponseCookie refreshTokenCookie  = ResponseCookie.from("refresh_token", tokenResponseDto.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 14) // 14일
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok().build();

    }

    @PostMapping("/refresh")
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
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Login Member member,
            HttpServletResponse response
    ) {
        authService.logout(member);

        expireCookie(response, "jwt_token");
        expireCookie(response, "refresh_token");

        return ResponseEntity.ok().build();
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .maxAge(0)
                .path("/")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
