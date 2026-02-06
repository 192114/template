package com.shadow.template.modules.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadow.template.common.result.Result;
import com.shadow.template.common.util.CookieUtils;
import com.shadow.template.common.util.RequestUtils;
import com.shadow.template.config.AppProperties;
import com.shadow.template.modules.auth.dto.RefreshTokenRequestCommand;
import com.shadow.template.modules.auth.dto.SendEmailDto;
import com.shadow.template.modules.auth.dto.UserLoginCommand;
import com.shadow.template.modules.auth.dto.UserLoginDto;
import com.shadow.template.modules.auth.dto.UserLogoutCommand;
import com.shadow.template.modules.auth.dto.UserRegisterDto;
import com.shadow.template.modules.auth.dto.UserTokenResult;
import com.shadow.template.modules.auth.enums.EmailUsageEnum;
import com.shadow.template.modules.auth.service.AuthService;
import com.shadow.template.modules.auth.service.EmailService;
import com.shadow.template.modules.auth.vo.TokenResponseVo;

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

  @Autowired
  private AppProperties appProperties;

  @PostMapping("/email")
  public Result<Void> send(@RequestBody @Valid SendEmailDto sendEmailDto) {
    final EmailUsageEnum usage = EmailUsageEnum.fromCode(sendEmailDto.getUsage());
    mailService.sendEmail(sendEmailDto.getEmail(), usage);

    return Result.success();
  }

  @PostMapping("/register")
  public Result<Void> register(@RequestBody @Valid UserRegisterDto userRegisterDto) {
    authService.register(userRegisterDto);
    return Result.success();
  }

  @PostMapping("/login")
  public Result<TokenResponseVo> login(@RequestBody @Valid UserLoginDto userLoginDto, HttpServletRequest request,
      HttpServletResponse response) {
    final UserLoginCommand userLoginCommand = new UserLoginCommand();
    userLoginCommand.setEmail(userLoginDto.getEmail());
    userLoginCommand.setPassword(userLoginDto.getPassword());
    userLoginCommand.setLoginType(userLoginDto.getLoginType());
    userLoginCommand.setEmailCode(userLoginDto.getEmailCode());
    userLoginCommand.setIpAddress(RequestUtils.getIpAddress(request));
    userLoginCommand.setUseragent(RequestUtils.getUserAgent(request));
    userLoginCommand.setDeviceId(RequestUtils.getDeviceId(request));
    UserTokenResult userTokenDto = authService.login(userLoginCommand);

    CookieUtils.setCookie(response, "refreshToken", userTokenDto.getRefreshToken(),
        appProperties.getRefresh().getExpireDays());

    final TokenResponseVo tokenResponseVo = new TokenResponseVo();

    tokenResponseVo.setToken(userTokenDto.getToken());

    return Result.success(tokenResponseVo);
  }

  @PostMapping("/refresh")
  public Result<TokenResponseVo> refresh(HttpServletRequest request,
      HttpServletResponse response) {
    final String refreshToken = CookieUtils.getCookie(request, "refreshToken");

    final String deviceId = RequestUtils.getDeviceId(request);
    final String useragent = RequestUtils.getUserAgent(request);
    final String ipAddress = RequestUtils.getIpAddress(request);

    final RefreshTokenRequestCommand refreshTokenRequestCommand = new RefreshTokenRequestCommand();
    refreshTokenRequestCommand.setRefreshToken(refreshToken);
    refreshTokenRequestCommand.setDeviceId(deviceId);
    refreshTokenRequestCommand.setUserAgent(useragent);
    refreshTokenRequestCommand.setIpAddress(ipAddress);

    final UserTokenResult userTokenDto = authService.refreshToken(refreshTokenRequestCommand);

    CookieUtils.setCookie(response, "refreshToken", userTokenDto.getRefreshToken(),
        appProperties.getRefresh().getExpireDays());

    final TokenResponseVo tokenResponseVo = new TokenResponseVo();

    tokenResponseVo.setToken(userTokenDto.getToken());

    return Result.success(tokenResponseVo);
  }

  @PostMapping("/logout")
  public Result<Void> logout(HttpServletRequest request,
      HttpServletResponse response) {
    final String refreshToken = CookieUtils.getCookie(request, "refreshToken");
    final UserLogoutCommand userLogoutDto = new UserLogoutCommand();
    final String deviceId = RequestUtils.getDeviceId(request);
    final String token = RequestUtils.getToken(request);
    userLogoutDto.setRefreshToken(refreshToken);
    userLogoutDto.setDeviceId(deviceId);
    userLogoutDto.setToken(token);
    authService.logout(userLogoutDto);
    return Result.success();
  }
}
