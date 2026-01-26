package com.shadow.template.modules.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum GenderEnum {
  UNKNOWN(0, "未知"),
  MALE(1, "男"),
  FEMALE(2, "女");

  @EnumValue // 标记数据库存储的是 code 字段
  @JsonValue // 标记返回前端 JSON 时显示的是 code (如果你想显示 "男"，就把这个注解移到 desc 上)
  private final int code;
  private final String desc;

  GenderEnum(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }
}
