package com.shadow.template.modules.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.ResultCode;
import com.shadow.template.modules.auth.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
  @Autowired
  private JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String from;

  /**
   * 构建 HTML 邮件内容
   */
  private String buildHtmlContent(String code) {
    return "<!DOCTYPE html>" +
        "<html>" +
        "<head>" +
        "<meta charset='UTF-8'>" +
        "<style>" +
        "  body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
        "  .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
        "  .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }"
        +
        "  .content { background-color: #f9f9f9; padding: 30px; border-radius: 0 0 5px 5px; }" +
        "  .code-box { background-color: #fff; border: 2px dashed #4CAF50; padding: 20px; text-align: center; margin: 20px 0; border-radius: 5px; }"
        +
        "  .code { font-size: 32px; font-weight: bold; color: #4CAF50; letter-spacing: 5px; }" +
        "  .footer { text-align: center; margin-top: 20px; color: #999; font-size: 12px; }" +
        "  .warning { color: #ff9800; font-size: 14px; margin-top: 15px; }" +
        "</style>" +
        "</head>" +
        "<body>" +
        "<div class='container'>" +
        "  <div class='header'>" +
        "    <h1>验证码邮件</h1>" +
        "  </div>" +
        "  <div class='content'>" +
        "    <p>尊敬的用户，您好！</p>" +
        "    <p>您正在使用邮箱验证功能，您的验证码是：</p>" +
        "    <div class='code-box'>" +
        "      <div class='code'>" + code + "</div>" +
        "    </div>" +
        "    <p class='warning'>⚠️ 验证码有效期为 10 分钟，请勿泄露给他人。</p>" +
        "    <p>如果这不是您的操作，请忽略此邮件。</p>" +
        "  </div>" +
        "  <div class='footer'>" +
        "    <p>此邮件由系统自动发送，请勿回复。</p>" +
        "  </div>" +
        "</div>" +
        "</body>" +
        "</html>";
  }

  @Override
  public void sendEmail(String to, String mailCode) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();

      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject("邮箱验证码");

      String htmlContent = buildHtmlContent(mailCode);

      helper.setText(htmlContent, true);

      javaMailSender.send(message);
    } catch (MessagingException e) {
      throw new BizException(ResultCode.EMAIL_SEND_FAILED);
    }
  }
}
