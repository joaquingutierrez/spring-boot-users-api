package com.joaquingutierrez.users_api.exceptions;

import com.joaquingutierrez.users_api.dtos.ErrorResponse;
import com.joaquingutierrez.users_api.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handlerException(ApiException ex) {
        return buildResponse(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handlerValidationError(MethodArgumentNotValidException ex) {
        Map<String, List<String>> fieldErr = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach((err)->
                        fieldErr.computeIfAbsent(err.getField(), k -> new ArrayList<>())
                                .add(err.getDefaultMessage())
                );

        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, fieldErr);
    }

    private ResponseEntity<ErrorResponse> buildResponse(ApiException ex) {
        ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                ex.getHttpStatus().value(),
                ex.getErrorCode(),
                ex.getErrorCode().getDefaultMessage()
        );
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(err);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, ErrorCode code, Map<String, List<String>> errs) {
        ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                code,
                code.getDefaultMessage(),
                errs
        );
        return ResponseEntity
                .status(status)
                .body(err);
    }
}
