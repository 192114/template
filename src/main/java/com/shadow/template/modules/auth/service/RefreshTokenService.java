package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.command.CreateSessionCommand;
import com.shadow.template.modules.auth.command.RefreshTokenRequestCommand;
import com.shadow.template.modules.auth.result.RefreshTokenRotateResult;

public interface RefreshTokenService {
  String generateRefreshToken();

  RefreshTokenRotateResult rotateRefreshToken(
      RefreshTokenRequestCommand refreshTokenRequestCommand);

  void revoke(String refreshToken, String deviceId);

  void revokeByUserId(Long userId, String deviceId);

  void createSession(CreateSessionCommand createSessionCommand);
}
