package com.beyond.easycheck.common.security.utils;

import com.beyond.easycheck.user.application.domain.EasyCheckUserDetails;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/*
[JWT 관련 메서드를 제공하는 클래스]
*/
@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    public final long accessTokenExpTime;
    public final long refreshTokenExpTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-exp-time}") long accessTokenExpTime,
            @Value("${jwt.refresh-token-exp-time}") long refreshTokenExpTime
    ) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );

        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    public String parseUserId(String token) {
        return Jwts.parser().verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", String.class);
    }

    public String parseEmail(String token) {
        return Jwts.parser().verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    public Collection<? extends GrantedAuthority> parseAuthorities(String token) {
        String authorities = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("authorities", String.class);
        log.info("[parseAuthorities] = {}", authorities);
        return stringToAuthorities(authorities);

    }

    public String createAccessToken(EasyCheckUserDetails member) {
        HashMap<String, String> claims = new HashMap<>();

        claims.put("userId", String.valueOf(member.getId()));
        claims.put("email", member.getUsername());
        claims.put("authorities", authoritiesToString(member.getAuthorities()));

        log.info("authorities = {}", claims);

        return createJwt(claims, accessTokenExpTime);

    }

    private String authoritiesToString(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private Collection<? extends GrantedAuthority> stringToAuthorities(String authoritiesString) {
        return Arrays.stream(authoritiesString.split(","))
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String createRefreshToken(EasyCheckUserDetails member) {
        HashMap<String, String> claims = new HashMap<>();

        claims.put("userId", String.valueOf(member.getId()));

        return createJwt(claims, accessTokenExpTime);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    private String createJwt(Map<String, String> claims, Long expiredMs) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

}

