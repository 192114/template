package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.dto.UserLoginDto;
import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.vo.LoginResponseVo;

public interface AuthService {
  LoginResponseVo login(UserLoginDto userLoginDto);

  void register(UserRegisterDto userRegisterDto);

  void refreshToken();
}
