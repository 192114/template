package com.shadow.template.modules.auth.service;

public interface MailService {
  void sendEmail(String to, String code);
}
