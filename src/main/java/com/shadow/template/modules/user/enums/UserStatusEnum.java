package com.shadow.template.modules.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum UserStatusEnum {
  DISABLED(0, "禁用"),
  ENABLE(1, "正常");

  @EnumValue
  @JsonValue
  private final int code;
  private final String desc;

  UserStatusEnum(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }
}
