package com.shadow.template.modules.auth.constants;

public final class AuthRegex {
  private AuthRegex() {}

  public static final String PASSWORD =
      "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

  public static final String EMAIL_CODE = "^[0-9]{6}$";
}