package com.shadow.template.modules.auth.dto;

import lombok.Data;

@Data
public class UserTokenResult {
  private String token;
  private String refreshToken;
}
