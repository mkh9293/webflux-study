package com.reactivespring.exceptionhandler;

import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Exception message is {}", ex.getMessage(), ex);

        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        var errorMessage = dataBufferFactory.wrap(ex.getMessage().getBytes());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if(ex instanceof ReviewDataException) {
            status = HttpStatus.BAD_REQUEST;
        } else if(ex instanceof ReviewNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }

        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().writeWith(Mono.just(errorMessage));
    }
}
