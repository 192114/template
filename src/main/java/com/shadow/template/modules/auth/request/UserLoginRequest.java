package com.shadow.template.modules.auth.request;

import com.shadow.template.modules.auth.validation.LoginConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@LoginConstraint
public class UserLoginRequest {
  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式不正确")
  private String email;

  private String password;
  private String loginType;
  private String emailCode;
}
