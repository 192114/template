package com.shadow.template.common.util;

import java.security.SecureRandom;

public class EmailCodeGenerator {
  private static final SecureRandom RANDOM = new SecureRandom();

  public String generateNumericCode(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(RANDOM.nextInt(10)); // 0-9
    }
    return sb.toString();
  }
}
