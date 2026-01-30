package com.shadow.template.modules.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.ResultCode;
import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.service.AuthService;
import com.shadow.template.modules.auth.vo.LoginResponseVo;
import com.shadow.template.modules.user.dto.UserCreateDto;

import com.shadow.template.modules.user.service.UserService;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private UserService userService;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Override
  public LoginResponseVo login(UserCreateDto userAuthDto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'login'");
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
  public void refreshToken() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
  }
  
}
