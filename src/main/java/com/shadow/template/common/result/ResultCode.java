package com.shadow.template.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {
  // 成功
  SUNCCESS(0, "成功"),

  // 通用错误码
  PARAM_ERROR(1000, "参数错误"),
  NOT_FOUND(1001, "资源不存在"),
  UNAUTHORIZED(1002, "未登录或无权限"),
  FORBIDDEN(1003, "禁止访问"),
  SYSTEM_ERROR(9999, "系统异常"),

  // 业务错误码（示例）
  USER_EMAIL_EXISTS(2001, "邮箱已存在"),
  EMAIL_SEND_FAILED(2002, "邮件发送失败");

  private final int code;
  private final String message;

  ResultCode(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
