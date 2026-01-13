package com.brajesh.moneymgmt.util;

import io.jsonwebtoken.*;
        import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtUtil(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration.access-ms}") long accessExpirationMs,
            @Value("${security.jwt.expiration.refresh-ms}") long refreshExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    // ------------------------------
    // Generate Access Token
    // ------------------------------

    // If we don't want to use role-based access
    public String generateAccessToken(String username) {
        return generateAccessToken(username, Map.of());
    }

    // If we have to use role-based access as well
    public String generateAccessToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(username)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ------------------------------
    // Generate Refresh Token
    // ------------------------------
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ------------------------------
    // Extract Username
    // ------------------------------
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ------------------------------
    // Extract Custom Claims
    // ------------------------------
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("Token expired", ex);
        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException ex) {
            throw new RuntimeException("Invalid token", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Token error", ex);
        }
    }

    // ------------------------------
    // Validate Token (Signature + Expiry)
    // ------------------------------
    public boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = extractUsername(token);
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // ------------------------------
    // Private Helpers
    // ------------------------------
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
