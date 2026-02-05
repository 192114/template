package com.shadow.template.modules.auth.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.ResultCode;
import com.shadow.template.config.AppProperties;
import com.shadow.template.modules.auth.dto.CreateSessionDto;
import com.shadow.template.modules.auth.dto.UserLoginDto;
import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.dto.UserTokenDto;
import com.shadow.template.modules.auth.enums.LoginTypeEnum;
import com.shadow.template.modules.auth.service.AuthService;
import com.shadow.template.modules.auth.service.RefreshTokenService;
import com.shadow.template.modules.user.dto.UserCreateDto;
import com.shadow.template.modules.user.entity.UserAuthEntity;
import com.shadow.template.modules.user.enums.UserStatusEnum;
import com.shadow.template.modules.user.service.UserService;
import com.shadow.template.security.JwtTokenProvider;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private UserService userService;

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Autowired
  private AppProperties appProperties;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  private UserTokenDto generateToken(Long userId) {
    // 生成 token
    final String token = jwtTokenProvider.generateToken(userId);
    // 生成
    final String refreshToken = refreshTokenService.generateRefreshToken();

    final CreateSessionDto createSessionDto = new CreateSessionDto();

    final LocalDateTime expireTime = LocalDateTime.now().plusDays(appProperties.getRefresh().getExpireDays());

    createSessionDto.setExpireTime(expireTime);
    createSessionDto.setUserId(userId);
    createSessionDto.setRefreshToken(refreshToken);

    refreshTokenService.createSession(createSessionDto);

    final UserTokenDto userTokenDto = new UserTokenDto();
    userTokenDto.setToken(token);
    userTokenDto.setRefreshToken(refreshToken);
    return userTokenDto;
  }

  @Override
  public UserTokenDto login(UserLoginDto userLoginDto) {
    final String email = userLoginDto.getEmail();
    final UserAuthEntity userAuthEntity = userService.getUserByEmail(email);

    final LoginTypeEnum loginTypeEnum = LoginTypeEnum.fromCode(userLoginDto.getLoginType());

    // 密码登录
    if (loginTypeEnum.getCode() == 1) {
      if (userAuthEntity == null) {
        throw new BizException(ResultCode.FAILED_PASSWORD_LOGIN);
      }

      if (userAuthEntity.getStatus() == UserStatusEnum.DISABLED) {
        throw new BizException(ResultCode.USER_DISABLED);
      }

      final String password = userLoginDto.getPassword();

      final String passwordHash = userAuthEntity.getPasswordHash();

      boolean matches = passwordEncoder.matches(password, passwordHash);
      if (!matches) {
        throw new BizException(ResultCode.FAILED_PASSWORD_LOGIN);
      }

      final UserTokenDto userTokenDto = generateToken(userAuthEntity.getId());
      return userTokenDto;
    }

    if (loginTypeEnum.getCode() == 2) {
      if (userAuthEntity == null) {
        throw new BizException(ResultCode.FAILED_EMAILCODE_LOGIN);
      }

      if (userAuthEntity.getStatus() == UserStatusEnum.DISABLED) {
        throw new BizException(ResultCode.USER_DISABLED);
      }

      final String code = stringRedisTemplate.opsForValue().get("code:email:LOGIN:" + email);

      if (code == null) {
        throw new BizException(ResultCode.EMAIL_CODE_EXPIRED);
      }

      if (!code.equals(userLoginDto.getEmailCode())) {
        throw new BizException(ResultCode.EMAIL_CODE_INCORRECT);
      }

      stringRedisTemplate.delete("code:email:LOGIN:" + userLoginDto.getEmail());

      final UserTokenDto userTokenDto = generateToken(userAuthEntity.getId());
      return userTokenDto;

    }

    throw new BizException(ResultCode.LOGIN_TYPE_ERROR);
  }

  @Override
  public void register(UserRegisterDto userRegisterDto) {
    final boolean isExist = userService.isExistByEmail(userRegisterDto.getEmail());
    if (isExist) {
      throw new BizException(ResultCode.USER_EMAIL_EXISTS);
    }

    final String code = stringRedisTemplate.opsForValue().get("code:email:REGISTER:" + userRegisterDto.getEmail());
    if (code == null) {
      throw new BizException(ResultCode.EMAIL_CODE_EXPIRED);
    }

    if (!code.equals(userRegisterDto.getEmailCode())) {
      throw new BizException(ResultCode.EMAIL_CODE_INCORRECT);
    }

    stringRedisTemplate.delete("code:email:REGISTER:" + userRegisterDto.getEmail());

    UserCreateDto userCreateDto = new UserCreateDto();
    userCreateDto.setEmail(userRegisterDto.getEmail());
    userCreateDto.setPassword(userRegisterDto.getPassword());
    userService.createUser(userCreateDto);
  }

  @Override
  public UserTokenDto refreshToken(String refreshToken, String deviceId, String useagent, String ipAddress) {
    final Long userId = refreshTokenService.verifyAndGetUserId(refreshToken, deviceId);

    final String nextRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken, deviceId, useagent, ipAddress);

    final String token = jwtTokenProvider.generateToken(userId);

    final UserTokenDto userTokenDto = new UserTokenDto();

    userTokenDto.setRefreshToken(nextRefreshToken);
    userTokenDto.setToken(token);

    return userTokenDto;
  }
}
