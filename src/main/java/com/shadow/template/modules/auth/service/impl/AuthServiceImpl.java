package com.shadow.template.modules.auth.service.impl;

import org.springframework.stereotype.Service;

import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.service.AuthService;
import com.shadow.template.modules.auth.vo.LoginResponseVo;
import com.shadow.template.modules.user.dto.UserCreateDto;

@Service
public class AuthServiceImpl implements AuthService {

  @Override
  public LoginResponseVo login(UserCreateDto userAuthDto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'login'");
  }

  @Override
  public void register(UserRegisterDto userRegisterDto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'register'");
  }

  @Override
  public void refreshToken() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
  }
  
}
