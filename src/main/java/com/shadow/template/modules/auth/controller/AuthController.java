package com.shadow.template.modules.auth.controller;

import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.Result;
import com.shadow.template.common.result.ResultCode;
import com.shadow.template.common.security.RateLimitService;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;
  private final EmailService mailService;
  private final AppProperties appProperties;
  private final RateLimitService rateLimitService;

  public AuthController(
      AuthService authService,
      EmailService mailService,
      AppProperties appProperties,
      RateLimitService rateLimitService) {
    this.authService = authService;
    this.mailService = mailService;
    this.appProperties = appProperties;
    this.rateLimitService = rateLimitService;
  }

  @PostMapping("/email")
  public Result<Void> send(
      @RequestBody @Valid SendEmailDto sendEmailDto, HttpServletRequest request) {
    final String ipAddress = RequestUtils.getIpAddress(request);
    rateLimitService.requireAllowed("auth:email:ip:" + ipAddress, 10, Duration.ofMinutes(1));
    rateLimitService.requireAllowed(
        "auth:email:target:" + sendEmailDto.getEmail(), 5, Duration.ofMinutes(10));

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
  public Result<TokenResponseVo> login(
      @RequestBody @Valid UserLoginDto userLoginDto,
      HttpServletRequest request,
      HttpServletResponse response) {
    final String ipAddress = RequestUtils.getIpAddress(request);
    rateLimitService.requireAllowed("auth:login:ip:" + ipAddress, 20, Duration.ofMinutes(1));
    rateLimitService.requireAllowed(
        "auth:login:email:" + userLoginDto.getEmail(), 10, Duration.ofMinutes(5));

    final UserLoginCommand userLoginCommand = new UserLoginCommand();
    userLoginCommand.setEmail(userLoginDto.getEmail());
    userLoginCommand.setPassword(userLoginDto.getPassword());
    userLoginCommand.setLoginType(userLoginDto.getLoginType());
    userLoginCommand.setEmailCode(userLoginDto.getEmailCode());
    userLoginCommand.setIpAddress(RequestUtils.getIpAddress(request));
    userLoginCommand.setUserAgent(RequestUtils.getUserAgent(request));
    userLoginCommand.setDeviceId(RequestUtils.getDeviceId(request));
    UserTokenResult userTokenDto = authService.login(userLoginCommand);

    CookieUtils.setCookie(
        response,
        "refreshToken",
        userTokenDto.getRefreshToken(),
        appProperties.getRefresh().getExpireDays());

    final TokenResponseVo tokenResponseVo = new TokenResponseVo();

    tokenResponseVo.setToken(userTokenDto.getToken());

    return Result.success(tokenResponseVo);
  }

  @PostMapping("/refresh")
  public Result<TokenResponseVo> refresh(HttpServletRequest request, HttpServletResponse response) {
    final String refreshToken = CookieUtils.getCookie(request, "refreshToken");
    final String deviceId = RequestUtils.getDeviceId(request);

    if (!StringUtils.hasText(refreshToken)) {
      throw new BizException(ResultCode.TOKEN_INVALID);
    }
    if (!StringUtils.hasText(deviceId)) {
      throw new BizException(ResultCode.PARAM_ERROR);
    }

    final String userAgent = RequestUtils.getUserAgent(request);
    final String ipAddress = RequestUtils.getIpAddress(request);
    rateLimitService.requireAllowed(
        "auth:refresh:device:" + deviceId + ":" + ipAddress, 30, Duration.ofMinutes(1));

    final RefreshTokenRequestCommand refreshTokenRequestCommand = new RefreshTokenRequestCommand();
    refreshTokenRequestCommand.setRefreshToken(refreshToken);
    refreshTokenRequestCommand.setDeviceId(deviceId);
    refreshTokenRequestCommand.setUserAgent(userAgent);
    refreshTokenRequestCommand.setIpAddress(ipAddress);

    final UserTokenResult userTokenDto = authService.refreshToken(refreshTokenRequestCommand);

    CookieUtils.setCookie(
        response,
        "refreshToken",
        userTokenDto.getRefreshToken(),
        appProperties.getRefresh().getExpireDays());

    final TokenResponseVo tokenResponseVo = new TokenResponseVo();

    tokenResponseVo.setToken(userTokenDto.getToken());

    return Result.success(tokenResponseVo);
  }

  @PostMapping("/logout")
  public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
    final String refreshToken = CookieUtils.getCookie(request, "refreshToken");
    final String deviceId = RequestUtils.getDeviceId(request);
    final String token = RequestUtils.getToken(request);

    if (!StringUtils.hasText(refreshToken) || !StringUtils.hasText(token)) {
      throw new BizException(ResultCode.TOKEN_INVALID);
    }
    if (!StringUtils.hasText(deviceId)) {
      throw new BizException(ResultCode.PARAM_ERROR);
    }

    final UserLogoutCommand userLogoutDto = new UserLogoutCommand();
    userLogoutDto.setRefreshToken(refreshToken);
    userLogoutDto.setDeviceId(deviceId);
    userLogoutDto.setToken(token);
    authService.logout(userLogoutDto);
    CookieUtils.deleteCookie(response, "refreshToken");
    return Result.success();
  }
}
