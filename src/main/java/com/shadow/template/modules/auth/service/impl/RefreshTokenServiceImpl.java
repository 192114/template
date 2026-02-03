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
import com.shadow.template.config.AppProperties;
import com.shadow.template.modules.auth.entity.UserSessionEntity;
import com.shadow.template.modules.auth.mapper.UserSessionMapper;
import com.shadow.template.modules.auth.service.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
  @Autowired
  private UserSessionMapper userSessionMapper;

  @Autowired
  private AppProperties appProperties;

  private UserSessionEntity getUserSessionEntityByRfreshToken(String refreshToken) {
    final String refreshtokenHash = DigestUtils.sha256Hex(refreshToken);
    LambdaQueryWrapper<UserSessionEntity> qw = Wrappers.lambdaQuery();
    UserSessionEntity userSessionEntity = userSessionMapper
        .selectOne(qw.eq(UserSessionEntity::getTokenHash, refreshtokenHash).eq(UserSessionEntity::getRevoked, false));

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
  public Long verifyAndGetUserId(String refreshToken) {
    UserSessionEntity userSessionEntity = getUserSessionEntityByRfreshToken(refreshToken);

    final LocalDateTime expireTime = userSessionEntity.getExpireTime();

    // token 过期了
    if (expireTime.isBefore(LocalDateTime.now())) {
      throw new BizException(ResultCode.TOKEN_EXPIRED);
    }

    return userSessionEntity.getUserId();
  }

  @Override
  public String rotateRefreshToken(String refreshToken) {
    final UserSessionEntity userSessionEntity = getUserSessionEntityByRfreshToken(refreshToken);

    final LocalDateTime expireTime = userSessionEntity.getExpireTime();

    // token 过期了
    if (expireTime.isBefore(LocalDateTime.now())) {
      throw new BizException(ResultCode.TOKEN_EXPIRED);
    }

    userSessionEntity.setRevoked(true);

    userSessionMapper.updateById(userSessionEntity);

    final String nextRefreshToken = generateRefreshToken();

    createSession(userSessionEntity.getUserId(), nextRefreshToken, userSessionEntity.getExpireTime());

    return nextRefreshToken;
  }

  @Override
  public void revoke(String refreshToken) {
    UserSessionEntity userSessionEntity = getUserSessionEntityByRfreshToken(refreshToken);

    userSessionEntity.setRevoked(true);

    userSessionMapper.updateById(userSessionEntity);
  }

  @Override
  public void createSession(Long userId, String refreshToken) {
    final Long expireDays = appProperties.getRefresh().getExpireDays();
    final LocalDateTime expireDateTime = LocalDateTime.now().plusDays(expireDays);
    createSession(userId, refreshToken, expireDateTime);
  }

  @Override
  public void createSession(Long userId, String refreshToken, LocalDateTime expireDateTime) {
    final UserSessionEntity userSessionEntity = new UserSessionEntity();

    userSessionEntity.setUserId(userId);
    userSessionEntity.setTokenHash(DigestUtils.sha256Hex(refreshToken));
    userSessionEntity.setExpireTime(expireDateTime);
    userSessionEntity.setRevoked(false);

    userSessionMapper.insert(userSessionEntity);
  }

}