package com.shadow.template.modules.user.dto;

import lombok.Data;

@Data
public class UserCreateCommand {
  private String email;
  private String password;
}
