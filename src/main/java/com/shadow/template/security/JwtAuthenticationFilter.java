package com.shadow.template.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.ResultCode;
import com.shadow.template.common.util.RequestUtils;
import com.shadow.template.modules.auth.service.TokenBlacklistService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  @Qualifier("handlerExceptionResolver")
  private HandlerExceptionResolver resolver;

  @Autowired
  private TokenBlacklistService tokenBlacklistService;

  private final JwtTokenProvider tokenProvider;

  public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = RequestUtils.getToken(request);
    if (StringUtils.hasText(token)) {
      try {

        String subject = tokenProvider.getTokenSubject(token);

        if (tokenBlacklistService.isTokenInBlacklist(token)) {
          resolver.resolveException(request, response, null, new BizException(ResultCode.TOKEN_INVALID));
          return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(subject, null,
            null);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (JwtException e) {
        if (e instanceof ExpiredJwtException) {
          resolver.resolveException(request, response, null, new BizException(ResultCode.TOKEN_EXPIRED));
        } else {
          resolver.resolveException(request, response, null, new BizException(ResultCode.TOKEN_INVALID));
        }
        return; // 终止过滤器链
      }
    }

    filterChain.doFilter(request, response);
  }
}