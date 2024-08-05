package com.tananushka.resource.proc.config.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class TransactionIdInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
    String traceId = request.getHeader("X-Trace-Id");
    if (traceId == null || traceId.isEmpty()) {
      traceId = UUID.randomUUID().toString();
    }
    MDC.put("traceId", traceId);
    request.setAttribute("traceId", traceId);
    return true;
  }

  @Override
  public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
    MDC.clear();
  }
}
