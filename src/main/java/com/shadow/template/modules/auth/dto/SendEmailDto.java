package com.shadow.template.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class SendEmailDto {
  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式不正确")
  private String email;

  @NotBlank(message = "邮件用途不能为空")
  String usage;
}
