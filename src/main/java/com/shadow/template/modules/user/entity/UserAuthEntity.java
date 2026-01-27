package com.shadow.template.modules.user.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shadow.template.modules.user.enums.UserStatusEnum;

import lombok.Data;

@TableName("user_auth")
@Data
public class UserAuthEntity {
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;
  private String email;
  private UserStatusEnum status;
  @TableField(value = "password_hash")
  private String passwordHash;
  @TableField(fill = FieldFill.INSERT, value = "create_time")
  private LocalDateTime createTime;
  @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
  private LocalDateTime updateTime;
}