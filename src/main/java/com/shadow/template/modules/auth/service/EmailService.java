package com.shadow.template.modules.auth.service;

public interface EmailService {
  void sendEmail(String to, String code);
}
