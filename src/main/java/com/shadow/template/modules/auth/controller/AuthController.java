package com.shadow.template.modules.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadow.template.common.result.Result;
import com.shadow.template.modules.auth.dto.SendEmailDto;
import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.service.AuthService;
import com.shadow.template.modules.auth.service.MailService;
import com.shadow.template.modules.auth.vo.SendMailVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  private AuthService authService;

  @Autowired
  private MailService mailService;

  @GetMapping("/mail")
  public Result<SendMailVo> send(@RequestParam SendEmailDto sendEmailDto) {
    mailService.sendEmail(sendEmailDto.getEmail(), "111111");
    SendMailVo sendMailVo = new SendMailVo();
    sendMailVo.setCode("111111");

    return Result.succuess(sendMailVo);
  }
  

  @PostMapping("/register")
  public Result<Void> register(@RequestBody UserRegisterDto userRegisterDto) {
    authService.register(userRegisterDto);
    return Result.succuess();
  }
}
