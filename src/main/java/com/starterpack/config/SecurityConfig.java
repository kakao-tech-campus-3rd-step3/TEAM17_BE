package com.starterpack.config;

import com.starterpack.auth.exception.CustomAccessDeniedHandler;
import com.starterpack.auth.exception.CustomAuthenticationEntryPoint;
import com.starterpack.auth.jwt.JwtTokenFilter;
import com.starterpack.auth.jwt.JwtTokenUtil;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenUtil  jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private static final String[] PUBLIC_URLS = {
            "/", // 루트 경로
            "/api/auth/**", // 로그인, 회원가입 등 인증 관련 API
            "/admin/**",
            "/actuator/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 세션을 사용하지 않으므로 STATELESS로 설정
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());

        // 폼 로그인 및 HTTP Basic 인증 비활성화
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // API 엔드포인트별 접근 권한 설정
        http.authorizeHttpRequests(auth -> auth
                        // .requestMatchers("/**").permitAll() // 개발 단계에선 이것만 주석 해제하고 아래는 주석 처리
                        .requestMatchers(PUBLIC_URLS).permitAll() // 배포 환경에선 아래 둘 주석 해제하기
                        .anyRequest().authenticated()
        );

        // 커스텀 필터 적용 (Spring Security의 기본 필터인 UsernamePasswordAuthenticationFilter 앞에 JwtTokenFilter 배치)
        http.addFilterBefore(
                new JwtTokenFilter(userDetailsService, jwtTokenUtil, Arrays.asList(PUBLIC_URLS)),
                UsernamePasswordAuthenticationFilter.class
        );

        //  인증/인가 관련 예외 처리 핸들러 등록
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(customAuthenticationEntryPoint) // 인증 실패 시(401)
                .accessDeniedHandler(customAccessDeniedHandler)           // 인가 실패 시(403)
        );

        return http.build();
    }

}
