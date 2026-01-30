package com.shadow.template.modules.auth.service;

import com.shadow.template.modules.auth.enums.EmailUsage;

public interface EmailService {
  String sendEmail(String to, EmailUsage usage);
}
