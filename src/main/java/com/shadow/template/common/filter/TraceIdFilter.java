package com.shadow.template.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter implements Filter {

  private static final String TRACE_ID_HEADER = "X-Trace-Id";
  private static final String TRACE_ID = "traceId";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    try {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
      if (traceId == null || traceId.isBlank()) {
        traceId = UUID.randomUUID().toString().replace("-", "");
      }
      MDC.put(TRACE_ID, traceId);
      chain.doFilter(request, response);
    } finally {
      MDC.remove(TRACE_ID);
    }
  }
}