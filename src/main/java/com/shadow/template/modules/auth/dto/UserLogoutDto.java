package com.shadow.template.modules.auth.dto;

import lombok.Data;

@Data
public class UserLogoutDto {
  private String refreshToken;
  private String deviceId;
  private String token;
}
