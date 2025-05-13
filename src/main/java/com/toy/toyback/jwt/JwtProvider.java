package com.toy.toyback.jwt;

import com.toy.toyback.code.AppRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtProvider {

    private static final long ACCESS_TOKEN_VALIDITY_MS = 1000 * 60 * 30;  // 30분
    private static final long REFRESH_TOKEN_VALIDITY_MS = 1000 * 60 * 60 * 24;  // 24시간

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    /**
     * JWT 암호화에 사용할 SecretKey 초기화
     */
    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ============================================================================
    // 토큰 생성
    // ============================================================================

    /**
     * Access Token 생성 (역할 포함)
     *
     * @param subject 토큰 소유자 식별값 (ex. clientId, userId 등)
     * @param role    사용자 권한 정보
     * @return Access Token 문자열
     */
    public String generateAccessToken(String subject, AppRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());
        claims.put("token_type", "access");
        return generateToken(subject, claims, ACCESS_TOKEN_VALIDITY_MS);
    }

    /**
     * Refresh Token 생성
     *
     * @param subject 토큰 소유자 식별값
     * @return Refresh Token 문자열
     */
    public String generateRefreshToken(String subject) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "refresh");
        return generateToken(subject, claims, REFRESH_TOKEN_VALIDITY_MS);
    }

    /**
     * JWT Token 생성 공통 메서드
     *
     * @param subject    토큰 소유자 식별값
     * @param claims     추가 클레임 정보
     * @param validityMs 유효 기간 (ms)
     * @return JWT 문자열
     */
    private String generateToken(String subject, Map<String, Object> claims, long validityMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    // ============================================================================
    // 토큰 검증 및 정보 추출
    // ============================================================================

    /**
     * JWT 유효성 검증
     *
     * @param token JWT 문자열
     * @return 유효한 경우 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException |
                ExpiredJwtException | UnsupportedJwtException |
                IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 subject 추출
     *
     * @param token JWT 문자열
     * @return subject 값
     */
    public String getSubject(String token) {
        return getClaim(token, Claims::getSubject);
    }

    /**
     * 토큰의 만료 시간 추출
     *
     * @param token JWT 문자열
     * @return 만료 일시
     */
    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    /**
     * 토큰의 타입(access / refresh) 추출
     *
     * @param token JWT 문자열
     * @return token_type 클레임 값
     */
    public String getTokenType(String token) {
        return getAllClaims(token).get("token_type", String.class);
    }

    /**
     * 토큰의 사용자 역할(role) 추출
     *
     * @param token JWT 문자열
     * @return role 클레임 값
     */
    public String getRole(String token) {
        return getAllClaims(token).get("role", String.class);
    }

    /**
     * 토큰에서 특정 클레임 정보 추출 (제네릭 처리)
     *
     * @param token    JWT 문자열
     * @param resolver 클레임 추출 함수
     * @param <T>      클레임 반환 타입
     * @return 추출된 값
     */
    private <T> T getClaim(String token, Function<Claims, T> resolver) {
        if (!validateToken(token)) return null;
        Claims claims = getAllClaims(token);
        return resolver.apply(claims);
    }

    /**
     * 토큰 전체 클레임 추출
     *
     * @param token JWT 문자열
     * @return Claims 객체
     */
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
