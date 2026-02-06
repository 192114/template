package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.dto.RefreshTokenRequestDto;
import com.shadow.template.modules.auth.dto.UserLoginDto;
import com.shadow.template.modules.auth.dto.UserLogoutDto;
import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.dto.UserTokenDto;

public interface AuthService {
  UserTokenDto login(UserLoginDto userLoginDto);

  void register(UserRegisterDto userRegisterDto);

  UserTokenDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto);

  void logout(UserLogoutDto userLogoutDto);
}