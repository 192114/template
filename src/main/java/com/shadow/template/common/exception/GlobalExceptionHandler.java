package com.shadow.template.common.exception;

import com.shadow.template.common.result.Result;
import com.shadow.template.common.result.ResultCode;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BizException.class)
  public ResponseEntity<Result<Void>> handleBizException(BizException ex) {
    return ResponseEntity.status(ex.getHttpStatus())
        .body(Result.failure(ex.getResultCode().getCode(), ex.getMessage()));
  }

  @ExceptionHandler({
      MethodArgumentNotValidException.class,
      BindException.class,
      ConstraintViolationException.class
  })
  public ResponseEntity<Result<Void>> handleValidationException(Exception ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Result.failure(ResultCode.PARAM_ERROR));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Result<Void>> handleException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Result.failure(ResultCode.SYSTEM_ERROR));
  }
}