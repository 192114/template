package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.dto.CreateSessionDto;
import com.shadow.template.modules.auth.dto.RefreshTokenRequestDto;

public interface RefreshTokenService {
  String generateRefreshToken();
  Long verifyAndGetUserId(String refreshToken, String deviceId);
  String rotateRefreshToken(RefreshTokenRequestDto refreshTokenRequestDto);
  void revoke(String refreshToken, String deviceId);
  void createSession(CreateSessionDto createSessionDto);
}
