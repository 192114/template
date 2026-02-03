package com.shadow.template.modules.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.ResultCode;
import com.shadow.template.modules.auth.dto.UserLoginDto;
import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.enums.LoginTypeEnum;
import com.shadow.template.modules.auth.service.AuthService;
import com.shadow.template.modules.auth.service.RefreshTokenService;
import com.shadow.template.modules.auth.vo.LoginResponseVo;
import com.shadow.template.modules.user.dto.UserCreateDto;
import com.shadow.template.modules.user.entity.UserAuthEntity;
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
  private JwtTokenProvider jwtTokenProvider;

  @Override
  public LoginResponseVo login(UserLoginDto userLoginDto) {
    final String email = userLoginDto.getEmail();
    final boolean isExist = userService.isExistByEmail(email);

    final LoginTypeEnum loginTypeEnum = LoginTypeEnum.fromCode(userLoginDto.getLoginType());

    // 密码登录
    if (loginTypeEnum.getCode() == 1) {
      if (!isExist) {
        throw new BizException(ResultCode.FAILED_PASSWORD_LOGIN);
      }

      final String password = userLoginDto.getPassword();

      final UserAuthEntity userAuthEntity = userService.getUserByEmail(email);

      final String passwordHash = userAuthEntity.getPasswordHash();

      boolean matches = passwordEncoder.matches(password, passwordHash);
      if (!matches) {
        throw new BizException(ResultCode.FAILED_PASSWORD_LOGIN);
      }

      // 生成 token
      final String token = jwtTokenProvider.generateToken(userAuthEntity.getId());
      // 生成
      final String refreshToken = refreshTokenService.generateRefreshToken();
      refreshTokenService.createSession(userAuthEntity.getId(), refreshToken);

      final LoginResponseVo loginResponseVo = new LoginResponseVo();
      loginResponseVo.setToken(token);
      loginResponseVo.setRefreshToken(refreshToken);
      return loginResponseVo;
    }

    if (loginTypeEnum.getCode() == 2) {
      if (!isExist) {
        throw new BizException(ResultCode.FAILED_EMAILCODE_LOGIN);
      }
    }

    return null;
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
  public LoginResponseVo refreshToken(String refreshToken) {
    final Long userId = refreshTokenService.verifyAndGetUserId(refreshToken);

    final String nextRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);

    final String token = jwtTokenProvider.generateToken(userId);

    final LoginResponseVo loginResponseVo = new LoginResponseVo();

    loginResponseVo.setRefreshToken(nextRefreshToken);
    loginResponseVo.setToken(token);

    return loginResponseVo;
  }
}
