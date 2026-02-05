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
  // 是否撤销 false 未撤销 true 撤销
  private Boolean revoked;
  @TableField(value = "revoked_time")
  private LocalDateTime revokedTime;
  @TableField(value = "parent_id")
  private Long parentId;
  @TableField(value = "device_id")
  private String deviceId;
  @TableField(value = "user_agent")
  private String userAgent;
  @TableField(value = "ip_address")
  private String ipAddress;
  
  @TableField(value = "expire_time")
  private LocalDateTime expireTime;
  @TableField(value = "create_time", fill = FieldFill.INSERT)
  private LocalDateTime createTime;
}
