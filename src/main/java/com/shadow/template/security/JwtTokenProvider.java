package com.shadow.template.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
  private final SecretKey key;
  private final String issuer;
  private final long expireSeconds;

  public JwtTokenProvider(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.issuer}") String issuer,
      @Value("${app.jwt.expire-seconds}") long expireSeconds) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.issuer = issuer;
    this.expireSeconds = expireSeconds;
  }

  // 生成 token
  public String generateToken(Long userId) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expireSeconds * 1000);

    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .issuer(issuer)
        .subject(String.valueOf(userId))
        .issuedAt(now)
        .expiration(exp)
        .signWith(key)
        .compact();
  }

  // 解析 token
  public Jws<Claims> parseToken(String token) throws JwtException {
    return Jwts.parser()
        .verifyWith(key)
        .requireIssuer(issuer)
        .build()
        .parseSignedClaims(token);
  }

  // 获取 subject userId
  public String getTokenSubject(String token) throws JwtException {
    return parseToken(token).getPayload().getSubject();
  }

  public long getExpireSeconds() {
    return expireSeconds;
  }
}
