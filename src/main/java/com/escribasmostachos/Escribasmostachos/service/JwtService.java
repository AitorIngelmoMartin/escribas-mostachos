package com.escribasmostachos.Escribasmostachos.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.security.AuthTokenFilter;

import java.security.Key;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void initKey() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(User user) {
        log.debug("generating token for user: " + user.getUsername());
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("email", user.getEmail())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public String getEmailFromToken(String token) {
        return getAllClaimsFromToken(token).get("email", String.class);
    }

    /**
     * Validates the given JWT token.
     * 
     * This method parses and validates the JWT token's signature, format, expiration,
     * and supported claims. If the token is invalid, malformed, expired, unsupported,
     * or contains empty claims, it will throw a corresponding exception.
     * 
     * The exceptions thrown by this method are caught by {@link AuthTokenFilter}
     * to return appropriate HTTP 401 Unauthorized responses with specific error messages.
     *
     * @param token the JWT token to validate
     * @throws io.jsonwebtoken.MalformedJwtException if the token is malformed
     * @throws io.jsonwebtoken.ExpiredJwtException if the token has expired
     * @throws io.jsonwebtoken.UnsupportedJwtException if the token is not supported
     * @throws IllegalArgumentException if the token claims string is empty
     * @throws io.jsonwebtoken.JwtException for other JWT-related validation errors
     */
    public void validateJwt(String token) {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
