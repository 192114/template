package com.shadow.template.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
  private int code;
  private String message;
  private T data;

  public static <T> Result<T> succuess(T data) {
    return new Result<>(ResultCode.SUNCCESS.getCode(), ResultCode.SUNCCESS.getMessage(), data);
  }

  public static Result<Void> succuess() {
    return succuess(null);
  }

  public static Result<Void> failure(ResultCode code) {
    return new Result<>(code.getCode(), code.getMessage(), null);
  }

  public static Result<Void> failure(int code, String message) {
    return new Result<>(code, message, null);
  }
}
