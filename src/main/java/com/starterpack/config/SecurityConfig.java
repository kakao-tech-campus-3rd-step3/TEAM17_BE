package com.starterpack.config;

import com.starterpack.auth.exception.CustomAccessDeniedHandler;
import com.starterpack.auth.exception.CustomAuthenticationEntryPoint;
import com.starterpack.auth.jwt.JwtTokenFilter;
import com.starterpack.auth.jwt.JwtTokenResolver;
import com.starterpack.auth.jwt.JwtTokenUtil;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtTokenResolver jwtTokenResolver;

    private static final String[] API_PUBLIC_URLS = {
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/kakao/callback",
            "/api/auth/csrf-token"
    };

    private static final String[] COMMON_PUBLIC_URLS = {
            "/",
            "/error",
            "/actuator/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/**"
    };

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/favicon.ico", "/.well-known/**", "/h2-console/**");
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        PlainTextCookieCsrfTokenRepository repository = new PlainTextCookieCsrfTokenRepository();
        repository.setCookieName("XSRF-TOKEN");
        repository.setHeaderName("X-XSRF-TOKEN");
        repository.setSecure(true);
        repository.setCookiePath("/");
        repository.setSameSite("None");
        return repository;
    }

    @Bean
    public CsrfTokenRequestHandler csrfTokenRequestHandler() {
        CsrfTokenRequestAttributeHandler handler = new CsrfTokenRequestAttributeHandler();
        handler.setCsrfRequestAttributeName("_csrf");
        return handler;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 STATELESS
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository())
                        .csrfTokenRequestHandler(csrfTokenRequestHandler())
                        .ignoringRequestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/kakao/callback",
                                "/api/auth/csrf-token"
                        )
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/debug/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/feeds", "/api/feeds/**", // feed 관련 GET 요청
                                "/api/starterPack/packs", "/api/starterPack/packs/**", "/api/api/starterPack/categories/**", // starterpack 관련 GET 요청
                                "/api/products", "/api/products/**",
                                "/api/categories", // 카테고리 조회 GET 요청
                                "/api/feeds/*/likes",
                                "/api/starterPack/packs/*/likes",
                                "/api/members/*/mypage" // 마이페이지 조회
                        ).permitAll()
                        .requestMatchers(API_PUBLIC_URLS).permitAll()
                        .requestMatchers(COMMON_PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtTokenFilter(userDetailsService, jwtTokenUtil,
                                Stream.of(API_PUBLIC_URLS, COMMON_PUBLIC_URLS, 
                                         new String[]{"/api/members/*/mypage"}).flatMap(Stream::of).toList(),
                                jwtTokenResolver),
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                );
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login").permitAll()
                        .anyRequest().hasRole("ADMIN")
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(
                new JwtTokenFilter(userDetailsService, jwtTokenUtil, Arrays.asList(COMMON_PUBLIC_URLS), jwtTokenResolver),
                UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                );
        return http.build();
    }


}
