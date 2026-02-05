package com.shadow.template.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {
  // 成功
  SUCCESS(0, "成功"),

  // 通用错误码
  PARAM_ERROR(1000, "参数错误"),
  NOT_FOUND(1001, "资源不存在"),
  UNAUTHORIZED(1002, "未登录或无权限"),
  FORBIDDEN(1003, "禁止访问"),
  TOKEN_EXPIRED(1004, "登录已过期"),
  TOKEN_INVALID(1005, "令牌无效"),
  SYSTEM_ERROR(9999, "系统异常"),

  // 业务错误码（示例）
  USER_EMAIL_EXISTS(2001, "邮箱已存在"),
  EMAIL_SEND_FAILED(2002, "邮件发送失败"),
  EMAIL_CODE_EXPIRED(2003, "邮箱验证码不存在或已过期"),
  EMAIL_CODE_INCORRECT(2004, "邮箱验证码不正确"),
  FAILED_PASSWORD_LOGIN(2005, "邮箱或密码错误"),
  FAILED_EMAILCODE_LOGIN(2006, "邮箱或验证码错误"),
  LOGIN_TYPE_ERROR(2007, "登录类型错误"),
  USER_DISABLED(2008, "用户已禁用");

  private final int code;
  private final String message;

  ResultCode(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
