package com.shadow.template.modules.auth.dto;

import lombok.Data;

@Data
public class UserRegisterDto {
  private String email;
  private String password;
  private String emailCode;
}
