package com.shadow.template.modules.auth.enums;

import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.ResultCode;

import lombok.Getter;

@Getter
public enum EmailUsage {
  REGISTER(1),
  LOGIN(2),
  RESET_PASSWORD(3),
  FORGET_PASSWORD(4);

  private final int code;

  EmailUsage(int code) {
    this.code = code;
  }

  public static EmailUsage fromCode(String code) {
    for (EmailUsage u : values()) {
      try {
        final int codeNumber = Integer.parseInt(code);
        if (u.code == codeNumber) {
          return u;
        }
      } catch (NumberFormatException error) {
        throw new BizException(ResultCode.PARAM_ERROR);
      }
    }
    throw new BizException(ResultCode.PARAM_ERROR);
  }
}
