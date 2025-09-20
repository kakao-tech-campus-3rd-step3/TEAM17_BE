package com.starterpack.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * API 요청을 가로채 JWT 토큰을 검증하는 필터
 * 요청 헤더의 JWT 토큰을 검증하고, 유효하다면 Spring Security 컨텍스트에
 * 인증된 사용자 정보(Authentication)를 등록함
 */
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    // 의존성 주입은 인터페이스 타입으로 해줌, 구현체는 스프링에서 자동으로 주입
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final List<String> publicUrls;
    private final JwtTokenResolver jwtTokenResolver;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // AntPathMatcher를 사용하여 현재 요청 URI가 공개 URL 패턴 중 하나와 일치하는지 확인
        return publicUrls.stream()
                .anyMatch(p -> pathMatcher.match(p, request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        jwtTokenResolver.resolveRequest(request)
                .ifPresent(this::authenticate);

        filterChain.doFilter(request, response);
    }

    private void authenticate(String token){
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmail(token);

            UserDetails memberDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    memberDetails,
                    null,
                    memberDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다.", email);
        }
    }

}
