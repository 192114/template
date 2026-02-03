package com.shadow.template.modules.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadow.template.common.result.Result;
import com.shadow.template.common.util.CookieUtils;
import com.shadow.template.config.AppProperties;
import com.shadow.template.modules.auth.dto.SendEmailDto;
import com.shadow.template.modules.auth.dto.UserLoginDto;
import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.enums.EmailUsageEnum;
import com.shadow.template.modules.auth.service.AuthService;
import com.shadow.template.modules.auth.service.EmailService;
import com.shadow.template.modules.auth.vo.LoginResponseVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  private AuthService authService;

  @Autowired
  private EmailService mailService;

  private AppProperties appProperties;

  @PostMapping("/email")
  public Result<Void> send(@RequestBody @Valid SendEmailDto sendEmailDto) {
    final EmailUsageEnum usage = EmailUsageEnum.fromCode(sendEmailDto.getUsage());
    mailService.sendEmail(sendEmailDto.getEmail(), usage);

    return Result.succuess();
  }

  @PostMapping("/register")
  public Result<Void> register(@RequestBody @Valid UserRegisterDto userRegisterDto) {
    authService.register(userRegisterDto);
    return Result.succuess();
  }

  @PostMapping("/login")
  public Result<LoginResponseVo> login(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request,
      HttpServletResponse response) {
    LoginResponseVo loginResponseVo = authService.login(userLoginDto);

    CookieUtils.setCookie(response, "refreshToken", loginResponseVo.getRefreshToken(),
        appProperties.getRefresh().getExpireDays());

    return Result.succuess(loginResponseVo);
  }

  @PostMapping("/refresh")
  public Result<LoginResponseVo> refresh(HttpServletRequest request,
      HttpServletResponse response) {
    final String refreshToken = CookieUtils.getCookie(request, "refreshToken");
    final LoginResponseVo loginResponseVo = authService.refreshToken(refreshToken);

    CookieUtils.setCookie(response, "refreshToken", loginResponseVo.getRefreshToken(),
        appProperties.getRefresh().getExpireDays());

    return Result.succuess(loginResponseVo);
  }

}
