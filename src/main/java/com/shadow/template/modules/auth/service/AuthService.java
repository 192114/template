package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.command.RefreshTokenRequestCommand;
import com.shadow.template.modules.auth.command.UserLoginCommand;
import com.shadow.template.modules.auth.command.UserLogoutCommand;
import com.shadow.template.modules.auth.request.UserRegisterRequest;
import com.shadow.template.modules.auth.result.UserTokenResult;

public interface AuthService {
  UserTokenResult login(UserLoginCommand userLoginCommand);

  void register(UserRegisterRequest userRegisterDto);

  UserTokenResult refreshToken(RefreshTokenRequestCommand refreshTokenRequestCommand);

  void logout(UserLogoutCommand userLogoutDto);
}
