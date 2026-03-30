package com.shadow.template.modules.user.command;

import lombok.Data;

@Data
public class UserCreateCommand {
  private String email;
  private String password;
}
