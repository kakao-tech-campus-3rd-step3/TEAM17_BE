package com.starterpack.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starterpack.exception.ErrorCode;
import com.starterpack.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Spring Security의 인증 실패(401 UNAUTHORIZED)를 처리하는 커스텀 핸들러
 * 관리자 페이지(/admin/로 시작하는 URL)로 접근하면 관리자 로그인 페이지로 리다이렉션
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException{
        log.warn("인증되지 않은 사용자 접근: {}", request.getRequestURI(), authException);

        // 관리자 페이지 URL인지 확인
        if (request.getRequestURI().startsWith("/admin/")) {
            // 관리자 페이지 URL이면 관리자 로그인 페이지로 리다이렉션
            response.sendRedirect("/admin/login");
            return;
        }

        // API 요청에 대해서는 JSON 응답 반환
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.UNAUTHORIZED);

        response.setStatus(ErrorCode.UNAUTHORIZED.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
