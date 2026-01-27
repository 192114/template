package com.shadow.template.modules.auth.vo;

import lombok.Data;

@Data
public class LoginResponseVo {
  private String token;
  private String refreshToken;
}
