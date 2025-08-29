package com.example.bankcards.security.service;

import com.example.bankcards.util.factory.JwtFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtFactory jwtFactory;

    @Value("${token.signing.key}")
    private String jwtSecret;

    @Value("${token.signing.expirationInMs}")
    private Long accessExpirationMs;

    @Value("${refreshToken.expirationInMs}")
    private Long refreshExpirationMs;

    public String generateAccessToken(String subject, Map<String, Object> extraClaims) {
        return jwtFactory.buildToken(extraClaims, subject, accessExpirationMs);
    }

    public String generateRefreshToken(String subject, Map<String, Object> extraClaims) {
        return jwtFactory.buildToken(extraClaims, subject, refreshExpirationMs);
    }

}
