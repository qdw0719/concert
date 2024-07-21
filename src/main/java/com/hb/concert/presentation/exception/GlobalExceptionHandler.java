package com.hb.concert.presentation.exception;

import com.hb.concert.domain.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice @Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorInfo> handleCustomException(CustomException ex, WebRequest request) {
        ErrorInfo errorInfo = new ErrorInfo(
                ex.getReason(),
                request.getDescription(false),
                request.getContextPath(),
                request.getParameterMap(),
                request.getHeader("User-Agent"),
                ex.getMethod(),
                ex.getClassName()
        );

        log.info("Exception occurred - Class: {}, Method: {}, Message: {}, Path: {}, Parameters: {}, UserAgent: {}",
                ex.getClassName(), ex.getMethod(), ex.getReason(), request.getContextPath(), request.getParameterMap(), request.getHeader("User-Agent"));

        return new ResponseEntity<>(errorInfo, ex.getStatusCode());
    }

    // Generic Exception Handler for other types of exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGlobalException(Exception ex, WebRequest request) {
        ErrorInfo errorInfo = new ErrorInfo(
                ex.getMessage(),
                request.getDescription(false),
                request.getContextPath(),
                request.getParameterMap(),
                request.getHeader("User-Agent"),
                null,
                null
        );

        log.info("Exception occurred - Message: {}, Path: {}, Parameters: {}, UserAgent: {}",
                ex.getMessage(), request.getContextPath(), request.getParameterMap(), request.getHeader("User-Agent"));

        return new ResponseEntity<>(errorInfo, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
