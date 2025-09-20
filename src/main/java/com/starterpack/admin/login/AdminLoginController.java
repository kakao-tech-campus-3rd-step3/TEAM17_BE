package com.starterpack.admin.login;

import com.starterpack.auth.dto.LocalLoginRequestDto;
import com.starterpack.auth.dto.TokenResponseDto;
import com.starterpack.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
        return "admin/login"; //
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

            Cookie cookie = new Cookie("jwt_token", tokenResponseDto.accessToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);

            return "redirect:/admin/members";
        } catch (Exception e) {
            return "redirect:/admin/login?error=true";
        }
    }
}
