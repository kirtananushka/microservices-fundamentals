package com.tananushka.gateway.svc.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-1)
public class GlobalErrorHandlingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).onErrorResume(error -> {

            if (error instanceof ResponseStatusException responseStatusException) {
                return handleResponseStatusException(exchange, responseStatusException);
            } else if (error instanceof ConnectException connectException) {
                return handleConnectException(exchange, connectException);
            }
            return Mono.error(error);
        });
    }

    private Mono<Void> handleResponseStatusException(ServerWebExchange exchange, ResponseStatusException ex) {
        HttpStatusCode status = ex.getStatusCode();
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", status.value());
        errorAttributes.put("message", ex.getMessage());
        errorAttributes.put("path", exchange.getRequest().getPath().toString());

        return exchange.getResponse().writeWith(Mono.fromSupplier(() -> {
            exchange.getResponse().setStatusCode(status);
            return getDataBuffer(exchange, errorAttributes);
        }));
    }

    private Mono<Void> handleConnectException(ServerWebExchange exchange, ConnectException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("message", ex.getMessage());
        errorAttributes.put("path", exchange.getRequest().getPath().toString());

        return exchange.getResponse().writeWith(Mono.fromSupplier(() -> {
            exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            return getDataBuffer(exchange, errorAttributes);
        }));
    }

    @NotNull
    private DataBuffer getDataBuffer(ServerWebExchange exchange, Map<String, Object> errorAttributes) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes;
        try {
            bytes = new ObjectMapper().writeValueAsBytes(errorAttributes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return exchange.getResponse().bufferFactory().wrap(bytes);
    }
}
