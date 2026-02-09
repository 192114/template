package com.shadow.template.modules.auth.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.ResultCode;
import com.shadow.template.config.AppProperties;
import com.shadow.template.modules.auth.dto.CreateSessionCommand;
import com.shadow.template.modules.auth.dto.RefreshTokenRequestCommand;
import com.shadow.template.modules.auth.dto.UserLoginCommand;
import com.shadow.template.modules.auth.dto.UserLogoutCommand;
import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.dto.UserTokenResult;
import com.shadow.template.modules.auth.enums.LoginTypeEnum;
import com.shadow.template.modules.auth.service.AuthService;
import com.shadow.template.modules.auth.service.RefreshTokenService;
import com.shadow.template.modules.auth.service.TokenBlacklistService;
import com.shadow.template.modules.user.dto.UserCreateCommand;
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
  private TokenBlacklistService tokenBlacklistService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Autowired
  private AppProperties appProperties;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  private UserTokenResult generateToken(Long userId, UserLoginCommand userLoginCommand) {
    // 如果再次登录 需撤销之前的登录
    refreshTokenService.revokeByUserId(userId, userLoginCommand.getDeviceId());

    // 生成 token
    final String token = jwtTokenProvider.generateToken(userId);
    // 生成
    final String refreshToken = refreshTokenService.generateRefreshToken();

    final CreateSessionCommand createSessionDto = new CreateSessionCommand();

    final LocalDateTime expireTime = LocalDateTime.now().plusDays(appProperties.getRefresh().getExpireDays());

    createSessionDto.setExpireTime(expireTime);
    createSessionDto.setUserId(userId);
    createSessionDto.setRefreshToken(refreshToken);
    createSessionDto.setIpAddress(userLoginCommand.getIpAddress());
    createSessionDto.setUseragent(userLoginCommand.getUseragent());
    createSessionDto.setDeviceId(userLoginCommand.getDeviceId());
    refreshTokenService.createSession(createSessionDto);

    final UserTokenResult userTokenDto = new UserTokenResult();
    userTokenDto.setToken(token);
    userTokenDto.setRefreshToken(refreshToken);
    return userTokenDto;
  }

  @Override
  public UserTokenResult login(UserLoginCommand userLoginCommand) {
    final String email = userLoginCommand.getEmail();
    final UserAuthEntity userAuthEntity = userService.getUserByEmail(email);

    final LoginTypeEnum loginTypeEnum = LoginTypeEnum.fromCode(userLoginCommand.getLoginType());

    // 密码登录
    if (loginTypeEnum.getCode() == 1) {
      if (userAuthEntity == null) {
        throw new BizException(ResultCode.FAILED_PASSWORD_LOGIN);
      }

      if (userAuthEntity.getStatus() == UserStatusEnum.DISABLED) {
        throw new BizException(ResultCode.USER_DISABLED);
      }

      final String password = userLoginCommand.getPassword();

      final String passwordHash = userAuthEntity.getPasswordHash();

      boolean matches = passwordEncoder.matches(password, passwordHash);
      if (!matches) {
        throw new BizException(ResultCode.FAILED_PASSWORD_LOGIN);
      }

      final UserTokenResult userTokenDto = generateToken(userAuthEntity.getId(), userLoginCommand);
      return userTokenDto;
    }

    // 邮箱验证码登录
    if (loginTypeEnum.getCode() == 2) {
      final String code = stringRedisTemplate.opsForValue().get("code:email:LOGIN:" + email);

      if (code == null) {
        throw new BizException(ResultCode.EMAIL_CODE_EXPIRED);
      }

      if (userAuthEntity == null) {
        throw new BizException(ResultCode.FAILED_EMAILCODE_LOGIN);
      }

      if (userAuthEntity.getStatus() == UserStatusEnum.DISABLED) {
        throw new BizException(ResultCode.USER_DISABLED);
      }

      if (!MessageDigest.isEqual(code.getBytes(StandardCharsets.UTF_8),
          userLoginCommand.getEmailCode().getBytes(StandardCharsets.UTF_8))) {
        throw new BizException(ResultCode.FAILED_EMAILCODE_LOGIN);
      }

      stringRedisTemplate.delete("code:email:LOGIN:" + userLoginCommand.getEmail());

      final UserTokenResult userTokenDto = generateToken(userAuthEntity.getId(), userLoginCommand);
      return userTokenDto;
    }

    throw new BizException(ResultCode.LOGIN_TYPE_ERROR);
  }

  @Override
  public void register(UserRegisterDto userRegisterDto) {
    final String code = stringRedisTemplate.opsForValue().get("code:email:REGISTER:" + userRegisterDto.getEmail());
    if (code == null) {
      throw new BizException(ResultCode.EMAIL_CODE_EXPIRED);
    }

    final boolean isExist = userService.isExistByEmail(userRegisterDto.getEmail());
    if (isExist) {
      throw new BizException(ResultCode.USER_EMAIL_EXISTS);
    }

    if (!code.equals(userRegisterDto.getEmailCode())) {
      throw new BizException(ResultCode.EMAIL_CODE_INCORRECT);
    }

    stringRedisTemplate.delete("code:email:REGISTER:" + userRegisterDto.getEmail());

    UserCreateCommand userCreateDto = new UserCreateCommand();
    userCreateDto.setEmail(userRegisterDto.getEmail());
    userCreateDto.setPassword(userRegisterDto.getPassword());
    userService.createUser(userCreateDto);
  }

  @Override
  public UserTokenResult refreshToken(RefreshTokenRequestCommand refreshTokenRequestCommand) {
    final Long userId = refreshTokenService.verifyAndGetUserId(refreshTokenRequestCommand.getRefreshToken(),
        refreshTokenRequestCommand.getDeviceId());

    final String nextRefreshToken = refreshTokenService.rotateRefreshToken(refreshTokenRequestCommand);

    final String token = jwtTokenProvider.generateToken(userId);

    final UserTokenResult userTokenDto = new UserTokenResult();

    userTokenDto.setRefreshToken(nextRefreshToken);
    userTokenDto.setToken(token);

    return userTokenDto;
  }

  @Override
  public void logout(UserLogoutCommand userLogoutDto) {
    refreshTokenService.revoke(userLogoutDto.getRefreshToken(), userLogoutDto.getDeviceId());
    tokenBlacklistService.addTokenToBlacklist(userLogoutDto.getToken());
  }
}
