package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.vo.LoginResponseVo;
import com.shadow.template.modules.user.dto.UserAuthDto;

public interface AuthService {
  LoginResponseVo login(UserAuthDto userAuthDto);

  void register(UserRegisterDto userRegisterDto);

  void refreshToken();
}
