package com.shadow.template.modules.auth.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CreateSessionCommand {
  private Long userId;
  private Long parentId;
  private LocalDateTime expireTime;
  private String refreshToken;
  private String ipAddress;
  private String useragent;
  private String deviceId;
}
