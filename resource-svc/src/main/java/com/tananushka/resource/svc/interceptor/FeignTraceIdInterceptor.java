package com.tananushka.resource.svc.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class FeignTraceIdInterceptor implements RequestInterceptor {
  public static final String TRACE_ID_HEADER = "X-Trace-Id";

  @Override
  public void apply(RequestTemplate template) {
    String traceId = MDC.get("traceId");
    if (traceId != null) {
      template.header(TRACE_ID_HEADER, traceId);
    }
  }
}
