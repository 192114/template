package com.shadow.template.modules.auth.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@TableName("user_session")
@Data
public class UserSessionEntity {
  @TableId(type = IdType.AUTO)
  private Long id;
  @TableField(value = "user_id")
  private Long userId;
  @TableField(value = "token_hash")
  private String tokenHash;
  private Boolean revoked;
  @TableField(value = "expire_time")
  private LocalDateTime expireTime;
  @TableField(value = "create_time", fill = FieldFill.INSERT)
  private LocalDateTime createTime;
  @TableField(value = "update_time", fill = FieldFill.UPDATE)
  private LocalDateTime updateTime;
}
