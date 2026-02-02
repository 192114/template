package com.shadow.template.modules.auth.enums;

import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.ResultCode;

import lombok.Getter;

@Getter
public enum LoginTypeEnum {
  PASSWORD(1),
  EMAIL_CODE(2);

  private final int code;

  LoginTypeEnum(int code) {
    this.code = code;
  }

  public static LoginTypeEnum fromCode(String code) {
    for (LoginTypeEnum u : values()) {
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
