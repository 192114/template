package com.shadow.template.modules.auth.service;

import java.time.LocalDateTime;

public interface RefreshTokenService {
  String generateRefreshToken();
  Long verifyAndGetUserId(String refreshToken);
  String rotateRefreshToken(String refreshToken);
  void revoke(String refreshToken);
  void createSession(Long userId, String refreshToken);
  void createSession(Long userId, String refreshToken, LocalDateTime expireDateTime);
}
