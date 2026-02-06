package com.shadow.template.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestCommand {
  @NotBlank(message = "refreshToken 不能为空")
  private String refreshToken;
  @NotBlank(message = "deviceId 不能为空")
  private String deviceId;
  private String userAgent;
  private String ipAddress;
}
