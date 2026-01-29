package com.shadow.template.modules.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadow.template.common.result.Result;
import com.shadow.template.modules.auth.dto.SendEmailDto;
import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.service.AuthService;
import com.shadow.template.modules.auth.service.EmailService;
import com.shadow.template.modules.auth.vo.SendEmailVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  private AuthService authService;

  @Autowired
  private EmailService mailService;

  @PostMapping("/email")
  public Result<SendEmailVo> send(@RequestBody @Valid SendEmailDto sendEmailDto) {
    mailService.sendEmail(sendEmailDto.getEmail(), "111111");
    SendEmailVo sendEmailVo = new SendEmailVo();
    sendEmailVo.setCode("111111");

    return Result.succuess(sendEmailVo);
  }
  

  @PostMapping("/register")
  public Result<Void> register(@RequestBody @Valid UserRegisterDto userRegisterDto) {
    authService.register(userRegisterDto);
    return Result.succuess();
  }
}
