package com.merchantsledger.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.merchantsledger.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  private final String secret;
  private final long expirationMillis;

  public JwtService(@Value("${app.jwt.secret}") String secret,
                    @Value("${app.jwt.expirationMinutes}") long expirationMinutes) {
    this.secret = secret;
    this.expirationMillis = expirationMinutes * 60 * 1000;
  }

  public String generateToken(User user) {
    String roles = user.getRoles().stream()
        .map(role -> role.getName().name())
        .collect(Collectors.joining(","));

    return Jwts.builder()
        .setSubject(user.getEmail())
        .claim("roles", roles)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
        .signWith(Keys.hmacShaKeyFor(normalizeSecret()), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token) {
    return getAllClaims(token).getSubject();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return getAllClaims(token).getExpiration().before(new Date());
  }

  private Claims getAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(normalizeSecret()))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private byte[] normalizeSecret() {
    byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
    if (raw.length >= 32) {
      return raw;
    }
    return Arrays.copyOf(raw, 32);
  }
}
