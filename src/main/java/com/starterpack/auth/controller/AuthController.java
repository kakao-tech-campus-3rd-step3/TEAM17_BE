package com.starterpack.auth.controller;

import com.starterpack.auth.dto.LocalLoginRequestDto;
import com.starterpack.auth.dto.TokenResponseDto;
import com.starterpack.auth.service.AuthService;
import com.starterpack.auth.dto.LocalSignUpRequestDto;
import com.starterpack.member.dto.MemberResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<TokenResponseDto> localLogin(
            @Valid @RequestBody LocalLoginRequestDto requestDto
    ) {
        TokenResponseDto tokenResponseDto = authService.localLogin(requestDto);
        return ResponseEntity.ok(tokenResponseDto);
    }

}
