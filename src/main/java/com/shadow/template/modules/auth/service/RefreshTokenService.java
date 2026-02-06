package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.dto.CreateSessionCommand;
import com.shadow.template.modules.auth.dto.RefreshTokenRequestCommand;

public interface RefreshTokenService {
  String generateRefreshToken();
  Long verifyAndGetUserId(String refreshToken, String deviceId);
  String rotateRefreshToken(RefreshTokenRequestCommand refreshTokenRequestCommand);
  void revoke(String refreshToken, String deviceId);
  void revokeByUserId(Long userId, String deviceId);
  void createSession(CreateSessionCommand createSessionCommand);
}
