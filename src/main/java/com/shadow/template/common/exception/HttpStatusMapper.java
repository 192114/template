package com.shadow.template.common.exception;

import com.shadow.template.common.result.ResultCode;
import org.springframework.http.HttpStatus;

public final class HttpStatusMapper {
  private HttpStatusMapper() {
  }

  public static HttpStatus fromResultCode(ResultCode code) {
    return switch (code) {
      // 成功
      case SUCCESS -> HttpStatus.OK;

      // 通用错误
      case PARAM_ERROR -> HttpStatus.BAD_REQUEST;
      case NOT_FOUND -> HttpStatus.NOT_FOUND;
      case UNAUTHORIZED, TOKEN_EXPIRED, TOKEN_INVALID -> HttpStatus.UNAUTHORIZED;
      case FORBIDDEN -> HttpStatus.FORBIDDEN;
      case SYSTEM_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;

      // 业务错误
      case USER_EMAIL_EXISTS -> HttpStatus.CONFLICT;
      case EMAIL_SEND_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR;
      case EMAIL_CODE_EXPIRED -> HttpStatus.BAD_REQUEST;
      case EMAIL_CODE_INCORRECT -> HttpStatus.BAD_REQUEST;
      case FAILED_PASSWORD_LOGIN -> HttpStatus.BAD_REQUEST;
      case FAILED_EMAILCODE_LOGIN -> HttpStatus.BAD_REQUEST;
      case LOGIN_TYPE_ERROR -> HttpStatus.BAD_REQUEST;
      case USER_DISABLED -> HttpStatus.FORBIDDEN;
    };
  }
}