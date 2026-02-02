package com.shadow.template.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import com.shadow.template.modules.auth.constants.AuthRegex;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserRegisterDto {
  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式不正确")
  private String email;
  @NotBlank(message = "密码不能为空")
  @Pattern(regexp = AuthRegex.PASSWORD, message = "密码至少8位，至少1个大写字母，1个小写字母，1个数字，1个特殊字符")
  private String password;
  @NotBlank(message = "邮箱验证码不能为空")
  @Pattern(regexp = AuthRegex.EMAIL_CODE, message = "邮箱验证码格式不正确")
  private String emailCode;
}
