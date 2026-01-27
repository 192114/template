// 业务错误
package com.shadow.template.common.exception;

import com.shadow.template.common.result.ResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
  private final ResultCode resultCode;
  private final HttpStatus httpStatus;

  public BizException(ResultCode resultCode) {
    super(resultCode.getMessage());
    this.resultCode = resultCode;
    this.httpStatus = HttpStatusMapper.fromResultCode(resultCode);
  }

  public ResultCode getResultCode() { return resultCode; }
  public HttpStatus getHttpStatus() { return httpStatus; }
}
