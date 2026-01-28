package com.shadow.template.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
  private final SecretKey key;
  private final String issuer;
  private final long expireSeconds;
  private final SecretKey refreshKey;
  private final long refreshExpireSeconds;

  public JwtTokenProvider(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.issuer}") String issuer,
      @Value("${app.jwt.expire-seconds}") long expireSeconds,
      @Value("${app.jwt.refresh-secret}") String refreshSecret,
      @Value("${app.jwt.refresh-expire-seconds}") long refreshExpireSeconds) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    this.issuer = issuer;
    this.expireSeconds = expireSeconds;
    this.refreshExpireSeconds = refreshExpireSeconds;
  }

  // 生成 token
  public String generateToken(Long userId) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expireSeconds * 1000);

    return Jwts.builder()
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

  // 生成refresh token
  public String generateRefreshToken(Long userId) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + refreshExpireSeconds * 1000);

    return Jwts.builder()
        .issuer(issuer)
        .subject(String.valueOf(userId))
        .issuedAt(now)
        .expiration(exp)
        .signWith(refreshKey)
        .compact();
  }

  // 解析 refresh token
  public Jws<Claims> parseRefreshToken(String refreshToken) throws JwtException {
    return Jwts.parser()
        .verifyWith(refreshKey)
        .requireIssuer(issuer)
        .build()
        .parseSignedClaims(refreshToken);
  }

  // 获取 refresh subject userId
  public String getRefreshTokenSubject(String refreshToken) throws JwtException {
    return parseRefreshToken(refreshToken).getPayload().getSubject();
  }

}
