package com.shadow.template.common.util;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtils {
  public static String getDeviceId(HttpServletRequest request) {
    // 约定从自定义 Header 获取，比如 "X-Device-Id"
    String deviceId = request.getHeader("X-Device-Id");
    if (deviceId == null || deviceId.isBlank()) {
      // 也可退回从参数或 Cookie 获取
      deviceId = request.getParameter("deviceId");
    }
    return deviceId;
  }

  public static String getUserAgent(HttpServletRequest request) {
    return request.getHeader("User-Agent");
  }

  public static String getIpAddress(HttpServletRequest request) {
    // 如果你有代理/CDN，优先从 X-Forwarded-For 取
    String xff = request.getHeader("X-Forwarded-For");
    if (xff != null && !xff.isBlank()) {
      // XFF 可能包含多个IP，取第一个
      return xff.split(",")[0].trim();
    }
    String realIp = request.getHeader("X-Real-IP");
    if (realIp != null && !realIp.isBlank()) {
      return realIp;
    }
    return request.getRemoteAddr();
  }

  public static String getToken(HttpServletRequest request) {
    String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }
    return null;
  }
}