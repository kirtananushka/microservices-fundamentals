package com.tananushka.resource.proc.config.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.listener.adapter.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Slf4j
@Component
public class JmsErrorHandler implements ErrorHandler {
    @Override
    public void handleError(Throwable t) {
        if (t instanceof ListenerExecutionFailedException exception) {
            log.error("Error processing message in JMS listener: {}", exception.getMessage());
        } else {
            log.error("Unexpected error occurred in JMS listener: {}", t.getMessage(), t);
        }
    }
}
