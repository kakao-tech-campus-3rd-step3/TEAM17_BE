package com.starterpack.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starterpack.exception.ErrorCode;
import com.starterpack.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Spring Security의 인가 실패(403 Forbidden)를 처리하는 커스텀 핸들러
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        log.warn("권한 없는 사용자 접근: {}", request.getRequestURI(), accessDeniedException);

        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.ACCESS_DENIED);

        response.setStatus(ErrorCode.ACCESS_DENIED.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
