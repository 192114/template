package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.dto.RefreshTokenRequestCommand;
import com.shadow.template.modules.auth.dto.UserLoginCommand;
import com.shadow.template.modules.auth.dto.UserLogoutCommand;
import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.dto.UserTokenResult;

public interface AuthService {
  UserTokenResult login(UserLoginCommand userLoginCommand);

  void register(UserRegisterDto userRegisterDto);

  UserTokenResult refreshToken(RefreshTokenRequestCommand refreshTokenRequestCommand);

  void logout(UserLogoutCommand userLogoutDto);
}