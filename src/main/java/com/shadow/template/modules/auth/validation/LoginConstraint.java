package com.shadow.template.modules.auth.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
// 确保运行时校验器能读取该注解
@Retention(RetentionPolicy.RUNTIME) 
@Documented
@Constraint(validatedBy = LoginConstraintValidator.class)
public @interface LoginConstraint {
  String message() default "登录参数不合法";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}