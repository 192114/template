package com.shadow.template.modules.auth.dto;

import lombok.Data;

@Data
public class UserLogoutCommand {
  private String refreshToken;
  private String deviceId;
  private String token;
}
