package com.shadow.template.modules.auth.command;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CreateSessionCommand {
  private Long userId;
  private Long parentId;
  private LocalDateTime expireTime;
  private String refreshToken;
  private String ipAddress;
  private String userAgent;
  private String deviceId;
}
