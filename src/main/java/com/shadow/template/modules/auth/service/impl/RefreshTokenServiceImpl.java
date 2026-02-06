package com.shadow.template.modules.auth.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.ResultCode;
import com.shadow.template.modules.auth.dto.CreateSessionCommand;
import com.shadow.template.modules.auth.dto.RefreshTokenRequestCommand;
import com.shadow.template.modules.auth.entity.UserSessionEntity;
import com.shadow.template.modules.auth.mapper.UserSessionMapper;
import com.shadow.template.modules.auth.service.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
  @Autowired
  private UserSessionMapper userSessionMapper;

  private UserSessionEntity getUserSessionEntityByRfreshToken(String refreshToken, String deviceId) {
    final String refreshtokenHash = DigestUtils.sha256Hex(refreshToken);
    LambdaQueryWrapper<UserSessionEntity> qw = Wrappers.lambdaQuery();
    UserSessionEntity userSessionEntity = userSessionMapper
        .selectOne(
            qw.eq(UserSessionEntity::getTokenHash, refreshtokenHash).eq(UserSessionEntity::getDeviceId, deviceId));

    if (userSessionEntity == null) {
      throw new BizException(ResultCode.TOKEN_INVALID);
    }

    return userSessionEntity;
  }

  @Override
  public String generateRefreshToken() {
    byte[] randomBytes = new byte[64];
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }

  @Override
  public Long verifyAndGetUserId(String refreshToken, String deviceId) {
    final UserSessionEntity userSessionEntity = getUserSessionEntityByRfreshToken(refreshToken, deviceId);

    final LocalDateTime expireTime = userSessionEntity.getExpireTime();
    final boolean revoked = userSessionEntity.getRevoked();

    if (revoked) {
      throw new BizException(ResultCode.TOKEN_INVALID);
    }

    // token 过期了
    if (expireTime.isBefore(LocalDateTime.now())) {
      throw new BizException(ResultCode.TOKEN_EXPIRED);
    }

    return userSessionEntity.getUserId();
  }

  @Override
  public String rotateRefreshToken(RefreshTokenRequestCommand refreshTokenRequestCommand) {
    final UserSessionEntity userSessionEntity = getUserSessionEntityByRfreshToken(refreshTokenRequestCommand.getRefreshToken(), refreshTokenRequestCommand.getDeviceId());

    final LocalDateTime expireTime = userSessionEntity.getExpireTime();
    final Long parentId = userSessionEntity.getId();
    final boolean revoked = userSessionEntity.getRevoked();
    final Long userId = userSessionEntity.getUserId();

    if (revoked) {
      throw new BizException(ResultCode.TOKEN_INVALID);
    }

    // token 过期了
    if (expireTime.isBefore(LocalDateTime.now())) {
      throw new BizException(ResultCode.TOKEN_EXPIRED);
    }

    userSessionEntity.setRevoked(true);

    userSessionMapper.updateById(userSessionEntity);

    final String nextRefreshToken = generateRefreshToken();

    final CreateSessionCommand createSessionCommand = new CreateSessionCommand();

    createSessionCommand.setUserId(userId);
    createSessionCommand.setParentId(parentId);
    createSessionCommand.setExpireTime(expireTime);
    createSessionCommand.setRefreshToken(nextRefreshToken);
    createSessionCommand.setDeviceId(refreshTokenRequestCommand.getDeviceId());
    createSessionCommand.setIpAddress(refreshTokenRequestCommand.getIpAddress());
    createSessionCommand.setUseragent(refreshTokenRequestCommand.getUserAgent());

    createSession(createSessionCommand);

    return nextRefreshToken;
  }

  @Override
  public void revoke(String refreshToken, String deviceId) {
    UserSessionEntity userSessionEntity = getUserSessionEntityByRfreshToken(refreshToken, deviceId);

    userSessionEntity.setRevoked(true);
    userSessionEntity.setRevokedTime(LocalDateTime.now());
    userSessionMapper.updateById(userSessionEntity);
  }

  @Override
  public void revokeByUserId(Long userId, String deviceId) {
    LambdaQueryWrapper<UserSessionEntity> qw = Wrappers.lambdaQuery();
    qw.eq(UserSessionEntity::getUserId, userId).eq(UserSessionEntity::getDeviceId, deviceId);
    UserSessionEntity userSessionEntity = userSessionMapper.selectOne(qw);
    if (userSessionEntity == null) {
      return;
    }
    userSessionEntity.setRevoked(true);
    userSessionEntity.setRevokedTime(LocalDateTime.now());
    userSessionMapper.updateById(userSessionEntity);
  }

  @Override
  public void createSession(CreateSessionCommand createSessionCommand) {
    final UserSessionEntity userSessionEntity = new UserSessionEntity();

    userSessionEntity.setUserId(createSessionCommand.getUserId());
    final String refreshTokenHash = DigestUtils.sha256Hex(createSessionCommand.getRefreshToken());
    userSessionEntity.setTokenHash(refreshTokenHash);
    userSessionEntity.setExpireTime(createSessionCommand.getExpireTime());
    userSessionEntity.setRevoked(false);
    userSessionEntity.setParentId(createSessionCommand.getParentId());
    userSessionEntity.setDeviceId(createSessionCommand.getDeviceId());
    userSessionEntity.setUserAgent(createSessionCommand.getUseragent());
    userSessionEntity.setIpAddress(createSessionCommand.getIpAddress());

    userSessionMapper.insert(userSessionEntity);
  }
}