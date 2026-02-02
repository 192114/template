package com.shadow.template.modules.auth.validation;

import java.util.regex.Pattern;

import com.shadow.template.modules.auth.constants.AuthRegex;
import com.shadow.template.modules.auth.dto.UserLoginDto;
import com.shadow.template.modules.auth.enums.LoginTypeEnum;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LoginConstraintValidator implements ConstraintValidator<LoginConstraint, UserLoginDto> {

  private static final Pattern EMAIL_CODE_PATTERN = Pattern.compile(AuthRegex.EMAIL_CODE);
  private static final Pattern PASSWORD_PATTERN = Pattern.compile(
      AuthRegex.PASSWORD);

  @Override
  public boolean isValid(UserLoginDto dto, ConstraintValidatorContext context) {
    if (dto == null) {
      return true;
    }

    LoginTypeEnum type;
    try {
      type = LoginTypeEnum.fromCode(dto.getLoginType()); // "1"/"2"
    } catch (Exception e) {
      return buildViolation(context, "登录方式不合法", "loginType");
    }

    if (type == LoginTypeEnum.PASSWORD) {
      if (isBlank(dto.getPassword())) {
        return buildViolation(context, "密码不能为空", "password");
      }
      if (!PASSWORD_PATTERN.matcher(dto.getPassword()).matches()) {
        return buildViolation(context, "密码格式不正确", "password");
      }
      return true;
    }

    if (type == LoginTypeEnum.EMAIL_CODE) {
      if (isBlank(dto.getEmailCode())) {
        return buildViolation(context, "邮箱验证码不能为空", "emailCode");
      }
      if (!EMAIL_CODE_PATTERN.matcher(dto.getEmailCode()).matches()) {
        return buildViolation(context, "邮箱验证码格式不正确", "emailCode");
      }
      return true;
    }

    return buildViolation(context, "登录方式不合法", "loginType");
  }

  private boolean buildViolation(ConstraintValidatorContext context, String msg, String field) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(msg)
        .addPropertyNode(field)
        .addConstraintViolation();
    return false;
  }

  private boolean isBlank(String v) {
    return v == null || v.trim().isEmpty();
  }
}