package com.shadow.template.modules.auth.dto;

import lombok.Data;

@Data
public class UserTokenDto {
  private String token;
  private String refreshToken;
}
