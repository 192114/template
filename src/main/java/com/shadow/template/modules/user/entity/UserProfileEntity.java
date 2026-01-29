package com.shadow.template.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shadow.template.modules.user.enums.GenderEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.time.LocalDateTime;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.IdType;

import lombok.Data;


@TableName("user_profile")
@Data
public class UserProfileEntity {
  @TableId(type = IdType.INPUT, value = "user_id")
  private Long userId;
  private String nickname;
  @TableField(value = "avatar_url")
  private String avatarUrl;
  private GenderEnum gender;
  private LocalDate birthday;
  private String bio;

  @TableField(fill = FieldFill.INSERT, value = "create_time")
  private LocalDateTime createTime;
  @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
  private LocalDateTime updateTime;
}