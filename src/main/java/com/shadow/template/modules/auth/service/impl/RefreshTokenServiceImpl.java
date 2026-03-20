package com.shadow.template.modules.auth.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private final UserSessionMapper userSessionMapper;

  public RefreshTokenServiceImpl(UserSessionMapper userSessionMapper) {
    this.userSessionMapper = userSessionMapper;
  }

  private UserSessionEntity getUserSessionEntityByRefreshToken(String refreshToken, String deviceId) {
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
    SECURE_RANDOM.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }

  @Override
  public Long verifyAndGetUserId(String refreshToken, String deviceId) {
    final UserSessionEntity userSessionEntity = getUserSessionEntityByRefreshToken(refreshToken, deviceId);

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
  @Transactional
  public String rotateRefreshToken(RefreshTokenRequestCommand refreshTokenRequestCommand) {
    final UserSessionEntity userSessionEntity = getUserSessionEntityByRefreshToken(
        refreshTokenRequestCommand.getRefreshToken(),
        refreshTokenRequestCommand.getDeviceId());

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
    createSessionCommand.setUserAgent(refreshTokenRequestCommand.getUserAgent());

    createSession(createSessionCommand);

    return nextRefreshToken;
  }

  @Override
  public void revoke(String refreshToken, String deviceId) {
    UserSessionEntity userSessionEntity = getUserSessionEntityByRefreshToken(refreshToken, deviceId);

    userSessionEntity.setRevoked(true);
    userSessionEntity.setRevokedTime(LocalDateTime.now());
    userSessionMapper.updateById(userSessionEntity);
  }

  @Override
  @Transactional
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
  @Transactional
  public void createSession(CreateSessionCommand createSessionCommand) {
    final UserSessionEntity userSessionEntity = new UserSessionEntity();

    userSessionEntity.setUserId(createSessionCommand.getUserId());
    final String refreshTokenHash = DigestUtils.sha256Hex(createSessionCommand.getRefreshToken());
    userSessionEntity.setTokenHash(refreshTokenHash);
    userSessionEntity.setExpireTime(createSessionCommand.getExpireTime());
    userSessionEntity.setRevoked(false);
    userSessionEntity.setParentId(createSessionCommand.getParentId());
    userSessionEntity.setDeviceId(createSessionCommand.getDeviceId());
    userSessionEntity.setUserAgent(createSessionCommand.getUserAgent());
    userSessionEntity.setIpAddress(createSessionCommand.getIpAddress());

    userSessionMapper.insert(userSessionEntity);
  }
}
