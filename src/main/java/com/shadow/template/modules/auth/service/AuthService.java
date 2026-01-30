package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.vo.LoginResponseVo;
import com.shadow.template.modules.user.dto.UserCreateDto;

public interface AuthService {
  LoginResponseVo login(UserCreateDto userAuthDto);

  void register(UserRegisterDto userRegisterDto);

  void refreshToken();
}
