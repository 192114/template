package com.shadow.template.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shadow.template.modules.user.enums.GenderEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.IdType;

import lombok.Data;


@TableName("user_profile")
@Data
public class UserProfileEntity {
  @TableId(type = IdType.INPUT)
  private Long user_id;
  @Length(max = 50, message = "昵称太长了")
  private String nickname;
  private String avatar_url;
  private GenderEnum gender;
  private LocalDate birthday;
  @Length(max = 1000, message = "简介不能超过1000字符")
  private String bio;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime create_time;
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime update_time;
}