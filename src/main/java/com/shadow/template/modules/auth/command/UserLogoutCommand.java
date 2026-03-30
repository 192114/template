package com.shadow.template.modules.auth.command;

import lombok.Data;

@Data
public class UserLogoutCommand {
  private String refreshToken;
  private String deviceId;
  private String token;
}
