package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.enums.EmailUsageEnum;

public interface EmailService {
  void sendEmail(String to, EmailUsageEnum usage);
}
