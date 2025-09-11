package com.starterpack.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 유효하지 않은 토큰이라면 Security Context에 등록하지 않고 다음 필터로 넘김
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("Authorization 헤더가 없거나 Bearer 타입이 아닙니다. URI: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        // 유효한 토큰이라면 email을 통해 MemberDetails(유저 정보)를 가져와서 authentication 등록
        // authentication을 Security Context에 저장하고 다음 필터로 넘김
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmail(token);

            UserDetails memberDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    memberDetails,
                    null,
                    memberDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다. URI: {}", email, request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }


}
