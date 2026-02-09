package com.shadow.template.modules.auth.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.ResultCode;
import com.shadow.template.common.util.EmailCodeGenerator;
import com.shadow.template.modules.auth.enums.EmailUsageEnum;
import com.shadow.template.modules.auth.service.EmailService;
import com.shadow.template.modules.user.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Autowired
  private UserService userService;

  @Value("${spring.mail.username}")
  private String from;

  /**
   * 构建 HTML 邮件内容
   */
  private String buildHtmlContent(String code) {
    return "<!DOCTYPE html>" +
        "<html lang='zh-CN'>" +
        "<head>" +
        "  <meta charset='UTF-8'>" +
        "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
        "  <title>验证码</title>" +
        "  <style>" +

        /* ===== 基础 ===== */
        "  body { margin: 0; padding: 0; background-color: #f2f4f8; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif; }"
        +
        "  .container { max-width: 600px; margin: 40px auto; padding: 0 16px; }" +

        /* ===== 卡片 ===== */
        "  .card { background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 12px 30px rgba(0,0,0,0.08); }"
        +

        /* ===== 头部渐变 ===== */
        "  .header { padding: 28px 20px; text-align: center; background: linear-gradient(135deg, #4CAF50, #2ecc71); color: #ffffff; }"
        +
        "  .header h1 { margin: 0; font-size: 22px; letter-spacing: 2px; }" +
        "  .header p { margin-top: 8px; font-size: 14px; opacity: 0.9; }" +

        /* ===== 内容 ===== */
        "  .content { padding: 32px 28px; color: #333; }" +
        "  .content p { font-size: 15px; line-height: 1.8; margin: 12px 0; }" +

        /* ===== 验证码框 ===== */
        "  .code-box { margin: 28px 0; padding: 22px; text-align: center; border-radius: 10px;" +
        "    background: #f7fdf9; border: 2px dashed #4CAF50; }" +
        "  .code { font-size: 34px; font-weight: 700; letter-spacing: 6px; color: #2ecc71; }" +

        /* ===== 提示 ===== */
        "  .warning { margin-top: 20px; padding: 14px 16px; background: #fff7e6; border-left: 4px solid #ffa726;" +
        "    font-size: 14px; color: #d97706; border-radius: 6px; }" +

        /* ===== 底部 ===== */
        "  .footer { padding: 20px; text-align: center; font-size: 12px; color: #999; background: #fafafa; }" +

        "  </style>" +
        "</head>" +
        "<body>" +

        "<div class='container'>" +
        "  <div class='card'>" +

        "    <div class='header'>" +
        "      <h1>邮箱验证码</h1>" +
        "      <p>Email Verification Code</p>" +
        "    </div>" +

        "    <div class='content'>" +
        "      <p>尊敬的用户，您好：</p>" +
        "      <p>您正在进行邮箱验证操作，请使用以下验证码完成验证：</p>" +

        "      <div class='code-box'>" +
        "        <div class='code'>" + code + "</div>" +
        "      </div>" +

        "      <div class='warning'>⚠️ 验证码 <b>5 分钟内有效</b>，请勿向任何人泄露。</div>" +
        "      <p>如果这不是您本人操作，请直接忽略本邮件。</p>" +
        "    </div>" +

        "    <div class='footer'>" +
        "      本邮件由系统自动发送，请勿回复" +
        "    </div>" +

        "  </div>" +
        "</div>" +

        "</body>" +
        "</html>";
  }

  @Override
  public void sendEmail(String to, EmailUsageEnum usage) {
    try {
      switch (usage) {
        case REGISTER:
          if (userService.isExistByEmail(to)) {
            throw new BizException(ResultCode.USER_EMAIL_EXISTS);
          }
          break;
        case LOGIN:
          if (!userService.isExistByEmail(to)) {
            throw new BizException(ResultCode.EMAIL_SEND_FAILED);
          }
          break;
        case RESET_PASSWORD:
        case FORGET_PASSWORD:
          if (!userService.isExistByEmail(to)) {
            throw new BizException(ResultCode.EMAIL_SEND_FAILED);
          }
          break;
        default:
          break;
      }

      MimeMessage message = javaMailSender.createMimeMessage();

      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject("邮箱验证码");

      EmailCodeGenerator emailCodeGenerator = new EmailCodeGenerator();

      final String emialCode = emailCodeGenerator.generateNumericCode(6);

      String htmlContent = buildHtmlContent(emialCode);

      helper.setText(htmlContent, true);

      javaMailSender.send(message);

      stringRedisTemplate.opsForValue().set("code:email:" + usage.name() + ":" + to, emialCode, 5, TimeUnit.MINUTES);
    } catch (MessagingException e) {
      throw new BizException(ResultCode.EMAIL_SEND_FAILED);
    }
  }
}
