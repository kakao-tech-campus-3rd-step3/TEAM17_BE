package com.starterpack.admin.login;

import com.starterpack.auth.dto.LocalLoginRequestDto;
import com.starterpack.auth.dto.TokenResponseDto;
import com.starterpack.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminLoginController {
    private final AuthService authService;
    

    /**
     * 관리자 로그인 페이지를 보여주는 메서드
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "admin/login";
    }

    /**
     * 관리자 로그인 폼 제출을 처리하는 메서드
     */
    @PostMapping("/login")
    public String processLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletResponse response
    ) {
        try {
            LocalLoginRequestDto loginRequestDto = new LocalLoginRequestDto(email, password);
            TokenResponseDto tokenResponseDto = authService.localLogin(loginRequestDto);

            ResponseCookie cookie = ResponseCookie.from("jwt_token", tokenResponseDto.accessToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(60 * 60)
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // 로그인 성공 시 스타터팩 관리 페이지로 리다이렉션
            return "redirect:/admin/packs";
        } catch (Exception e) {
            return "redirect:/admin/login?error=true";
        }
    }
}
