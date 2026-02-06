package com.shadow.template.modules.auth.dto;

import lombok.Data;

@Data
public class UserLoginCommand {
  private String email;
  private String password;
  private String loginType;
  private String emailCode;
  private String ipAddress;
  private String useragent;
  private String deviceId;
}
