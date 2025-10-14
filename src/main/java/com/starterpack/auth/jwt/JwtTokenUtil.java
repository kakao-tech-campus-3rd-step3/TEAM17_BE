package com.starterpack.auth.jwt;

import com.starterpack.member.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenUtil {

    private final String secretKeyString;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    private Key signingKey;

    // @Value 어노테이션으로 .env의 값을 주입받음
    public JwtTokenUtil(
            @Value("${jwt.secret-key}") String secretKeyString,
            @Value("${jwt.access-expiration-ms}") long accessExpirationMs,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs
    ) {
        this.secretKeyString = secretKeyString;
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    /**
     * 의존성 주입 후 초기화를 수행하는 메서드.
     * Base64로 인코딩된 secretKey를 Key 객체로 변환합니다.
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String email, Role role) {
        return createToken(email, role, accessExpirationMs);
    }

    public String createRefreshToken(String email) {
        return createToken(email, null, refreshExpirationMs);
    }
    /**
     * JWT 토큰을 생성
     *
     * @param email 토큰의 주체(subject)가 될 사용자의 이메일
     * @param role  사용자의 권한 정보
     * @return 생성된 JWT 토큰
     */
    public String createToken(String email, Role role, long expirationMs) {
        Claims claims = Jwts.claims();
        if (role != null) {
            claims.put("role", role.getKey());
        }

        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 사용자 이메일(Subject)을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 추출된 이메일
     */
    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 토큰의 유효성을 검증합니다.
     *
     * @param token JWT 토큰
     * @return 토큰이 유효하면 true
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("유효하지 않은 JWT 토큰입니다. {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 Claims 정보를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 추출된 Claims
     * @throws JwtException 토큰 파싱 중 오류 발생 시
     */
    private Claims getClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
