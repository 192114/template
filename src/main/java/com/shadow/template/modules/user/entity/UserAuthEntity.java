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
  
  private String password_hash;
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime create_time;
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime update_time;
}