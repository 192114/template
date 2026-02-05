package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.dto.UserLoginDto;
import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.dto.UserTokenDto;

public interface AuthService {
  UserTokenDto login(UserLoginDto userLoginDto);

  void register(UserRegisterDto userRegisterDto);

  UserTokenDto refreshToken(String refreshToken, String deviceId, String useragent, String ipAddress);
}
