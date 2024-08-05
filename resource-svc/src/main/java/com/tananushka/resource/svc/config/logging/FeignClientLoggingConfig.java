package com.tananushka.resource.svc.config.logging;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientLoggingConfig {
  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      String traceId = MDC.get("traceId");
      if (traceId != null) {
        requestTemplate.header("X-Trace-Id", traceId);
      }
    };
  }
}
