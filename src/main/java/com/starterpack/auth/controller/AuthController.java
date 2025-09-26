package com.starterpack.auth.controller;

import com.starterpack.auth.dto.LocalLoginRequestDto;
import com.starterpack.auth.dto.TokenResponseDto;
import com.starterpack.auth.service.AuthService;
import com.starterpack.auth.dto.LocalSignUpRequestDto;
import com.starterpack.member.dto.MemberResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
        String accessToken = tokenResponseDto.accessToken();

        ResponseCookie cookie = ResponseCookie.from("jwt_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60) // 1시간
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();

    }

}
