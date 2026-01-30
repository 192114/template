package com.shadow.template.modules.user.dto;

import lombok.Data;

@Data
public class UserCreateDto {
  private String email;
  private String password;
}
