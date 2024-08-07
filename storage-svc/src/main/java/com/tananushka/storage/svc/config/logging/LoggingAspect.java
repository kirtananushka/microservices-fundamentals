package com.tananushka.storage.svc.config.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void restController() {
  }

  @AfterReturning(pointcut = "restController()", returning = "result")
  public void logAfterReturning(JoinPoint joinPoint, Object result) {
    log.info("Endpoint: {}, Response: {}", joinPoint.getSignature().toShortString(), result);
  }

  @AfterThrowing(pointcut = "restController()", throwing = "e")
  public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
    log.error("Endpoint: {}, Error: {}", joinPoint.getSignature().toShortString(), e.getMessage());
  }
}
