package com.shadow.template.common.exception;

import com.shadow.template.common.result.Result;
import com.shadow.template.common.result.ResultCode;
import com.shadow.template.common.result.FieldErrorDetail;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BizException.class)
  public ResponseEntity<Result<Void>> handleBizException(BizException ex) {
    log.warn("biz exception: code={}, msg={}", ex.getResultCode(), ex.getMessage());
    return ResponseEntity.status(ex.getHttpStatus())
        .body(Result.failure(ex.getResultCode().getCode(), ex.getMessage()));
  }

  /**
   * @RequestBody + @Valid 触发
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Result<List<FieldErrorDetail>>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    List<FieldErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(err -> new FieldErrorDetail(err.getField(), err.getDefaultMessage()))
        .collect(Collectors.toList());

    log.warn("validation error: {}", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new Result<>(ResultCode.PARAM_ERROR.getCode(), ResultCode.PARAM_ERROR.getMessage(), errors));
  }

  /**
   * @ModelAttribute / 表单参数 触发
   */
  @ExceptionHandler(BindException.class)
  public ResponseEntity<Result<List<FieldErrorDetail>>> handleBindException(BindException ex) {
    List<FieldErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(err -> new FieldErrorDetail(err.getField(), err.getDefaultMessage()))
        .collect(Collectors.toList());

    log.warn("bind error: {}", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new Result<>(ResultCode.PARAM_ERROR.getCode(), ResultCode.PARAM_ERROR.getMessage(), errors));
  }

  /**
   * @RequestParam / @PathVariable 触发
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Result<List<FieldErrorDetail>>> handleConstraintViolation(ConstraintViolationException ex) {
    List<FieldErrorDetail> errors = ex.getConstraintViolations().stream()
        .map(v -> new FieldErrorDetail(v.getPropertyPath().toString(), v.getMessage()))
        .collect(Collectors.toList());

    log.warn("constraint violation: {}", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new Result<>(ResultCode.PARAM_ERROR.getCode(), ResultCode.PARAM_ERROR.getMessage(), errors));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Result<Void>> handleException(Exception ex) {
    log.error("system exception: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Result.failure(ResultCode.SYSTEM_ERROR));
  }
}