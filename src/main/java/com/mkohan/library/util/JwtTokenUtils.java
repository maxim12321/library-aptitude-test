package com.mkohan.library.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JwtTokenUtils {

    private static final Key JWT_SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static Optional<String> tryExtractUsername(String token) {
        try {
            return Optional.of(extractUsername(token));
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    public static String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .ifPresent(role -> claims.put("role", role));
        return createToken(userDetails.getUsername(), claims);
    }

    public static Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private static String extractUsername(String token) throws JwtException {
        return extractAllClaims(token).getSubject();
    }

    private static Boolean isTokenExpired(String token) throws JwtException {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private static Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private static String createToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(JWT_SECRET)
                .compact();
    }
}
