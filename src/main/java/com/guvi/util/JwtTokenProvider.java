package com.guvi.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // ✅ From application.properties
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expirationMinutes}")
    private long jwtExpirationMinutes;

    // ✅ Generate signing key from property
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // ✅ Generate Token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // stored in "sub"
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMinutes * 60 * 1000)) // minutes → ms
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Extract Username
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject(); // ✅ FIX
    }

    // ✅ Validate Token
    public boolean validateToken(String token) {
        try {
            getClaims(token); // will throw exception if invalid
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Common method to extract claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // ✅ SAME key used everywhere
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}