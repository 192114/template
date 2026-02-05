package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.dto.CreateSessionDto;

public interface RefreshTokenService {
  String generateRefreshToken();
  Long verifyAndGetUserId(String refreshToken, String deviceId);
  String rotateRefreshToken(String refreshToken, String deviceId, String useragent, String ipAddress);
  void revoke(String refreshToken, String deviceId);
  void createSession(CreateSessionDto createSessionDto);
}
