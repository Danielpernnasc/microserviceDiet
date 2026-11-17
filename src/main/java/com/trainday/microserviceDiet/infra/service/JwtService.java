package com.trainday.microserviceDiet.infra.service;

import com.trainday.microserviceDiet.models.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getKey() {

        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Key testgetKey() {

        return getKey();
    }

    public String generateToken(
            String email,
            String crn,
            String professionalId, Role role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("crn", crn);
        claims.put("email", email);
        claims.put("professionalId", professionalId);
        claims.put("role", role.name());
        System.out.println("Subject = " + crn);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(crn)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
