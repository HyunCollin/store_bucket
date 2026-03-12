package com.store.store_bucket.service;

import com.store.store_bucket.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private Key key;

    @PostConstruct
    protected void init() {
        // 문자열 키를 HMAC-SHA 알고리즘에 적합한 Key 객체로 변환
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 생성
     * @param userId
     * @return
     */
    public String createToken(String userId) {
        // 10분 (밀리초 단위)
        long tokenValidTime = 10 * 60 * 1000L;
        Date now = new Date();

        UserRole userRole = UserRole.getRoleByUserId(userId);

        return Jwts.builder()
                .setSubject(userId)
                .claim("roles", userRole.getValue())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 유효성, 만료일자 확인
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    // ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 서명이 다르거나, 만료되었거나, 형식이 잘못된 경우 모두 포함
            log.error("유효하지 않은 토큰입니다: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰에서 권한 정보 추출
     */
    public String getUserRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("roles", String.class);
    }
}