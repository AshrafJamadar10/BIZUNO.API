package com.bizuno.utils;

import com.bizuno.dtos.main.UserDO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private SecretKey jwtSecretKey;

    @Value("${jwt.secret}")
    public void setJwtSecret(String secret) {
        this.jwtSecretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    public String generateAccessToken(String phoneOrEmail, String userId, String userType, String businessCode) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phoneOrEmail", phoneOrEmail);
        claims.put("userType", userType);
        claims.put("businessCode", businessCode);

        return Jwts.builder()
                .claims(claims)
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(jwtSecretKey)
                .compact();
    }

    public String generateRefreshToken(String phoneOrEmail, String code) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phoneOrEmail", phoneOrEmail);
        claims.put("code", code);

        return Jwts.builder()
                .claims(claims)
                .subject(phoneOrEmail)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(jwtSecretKey)
                .compact();
    }

    public UserDO getUserDataFromToken(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(jwtSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return UserDO.builder()
                .userId(UUID.fromString(claims.getSubject()))
                .userType(claims.get("userType", String.class))
                .phoneOrEmail(claims.get("phoneOrEmail", String.class))
                .businessCode(claims.get("businessCode", String.class))
                .packageScopes(
                        claims.get("packageScopes") != null
                                ? ((List<?>) claims.get("packageScopes"))
                                .stream()
                                .map(String::valueOf)
                                .collect(Collectors.toSet())
                                : new HashSet<>()
                )
                .build();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (JwtException e) {
            return false;
        }
    }

}

