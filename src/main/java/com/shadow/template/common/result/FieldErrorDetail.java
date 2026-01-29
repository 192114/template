package com.shadow.template.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldErrorDetail {
  private String field;
  private String message;
}
