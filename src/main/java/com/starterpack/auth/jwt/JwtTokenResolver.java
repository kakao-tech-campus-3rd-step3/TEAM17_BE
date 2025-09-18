package com.starterpack.auth.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * HttpServletRequest에서 JWT 토큰을 추출하는 책임을 가지는 클래스
 */
@Component
public class JwtTokenResolver {
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 요청(Request)에서 JWT 토큰을 추출합니다. (쿠키 -> 헤더 순으로 확인)
     * @param request HttpServletRequest
     * @return 토큰이 존재하면 Optional<String>, 없으면 Optional.empty()
     */
    public Optional<String> resolveRequest(HttpServletRequest request) {
        return resolveFromCookie(request)
                .or(() -> resolveFromHeader(request));
    }

    private Optional<String> resolveFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "jwt_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private Optional<String> resolveFromHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return Optional.of(header.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

}
