package com.shadow.template.modules.auth.result;

import lombok.Data;

@Data
public class RefreshTokenRotateResult {
  private Long userId;
  private String refreshToken;
}
