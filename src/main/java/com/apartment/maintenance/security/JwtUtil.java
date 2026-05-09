package com.apartment.maintenance.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    // MUST be base64 encoded
    private final String SECRET =
            Base64.getEncoder()
                    .encodeToString("maintenance-secret-key-very-secure".getBytes());

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ Generate JWT
    public String generateToken(UUID userId) {

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSigningKey())
                .compact();
    }

    // ✅ Extract USER ID from token
    public UUID extractUserId(String token) {

        String subject = extractClaims(token).getSubject();

        return UUID.fromString(subject);
    }

    // ✅ Validate token
    public boolean validateToken(String token) {
        extractClaims(token);
        return true;
    }

    // ✅ Parse claims
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}