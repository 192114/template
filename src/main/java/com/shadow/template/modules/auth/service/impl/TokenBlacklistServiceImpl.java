package com.shadow.template.modules.auth.service.impl;

import com.shadow.template.modules.auth.service.TokenBlacklistService;
import com.shadow.template.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
  private final StringRedisTemplate stringRedisTemplate;
  private final JwtTokenProvider jwtTokenProvider;

  public TokenBlacklistServiceImpl(
      StringRedisTemplate stringRedisTemplate, JwtTokenProvider jwtTokenProvider) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public void addTokenToBlacklist(String token) {
    Jws<Claims> decodedJWT = jwtTokenProvider.parseToken(token);

    final String key = "auth:blacklist:" + decodedJWT.getPayload().getId();

    Instant expiration = decodedJWT.getPayload().getExpiration().toInstant();

    long ttlMillis = expiration.toEpochMilli() - Instant.now().toEpochMilli();

    stringRedisTemplate.opsForValue().set(key, "1", ttlMillis, TimeUnit.MILLISECONDS);
  }

  @Override
  public boolean isTokenInBlacklist(String token) {
    Jws<Claims> decodedJWT = jwtTokenProvider.parseToken(token);
    final String key = "auth:blacklist:" + decodedJWT.getPayload().getId();

    return stringRedisTemplate.hasKey(key);
  }
}
