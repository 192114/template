package com.shadow.template.modules.auth.command;

import lombok.Data;

@Data
public class UserLoginCommand {
  private String email;
  private String password;
  private String loginType;
  private String emailCode;
  private String ipAddress;
  private String userAgent;
  private String deviceId;
}
