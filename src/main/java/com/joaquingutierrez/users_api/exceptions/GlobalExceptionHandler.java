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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerUserNotFound(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getDefaultMessage());
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handlerEmailAlreadyExist(EmailAlreadyExistException ex) {
        return buildResponse(HttpStatus.CONFLICT, ErrorCode.EMAIL_ALREADY_EXISTS, ErrorCode.EMAIL_ALREADY_EXISTS.getDefaultMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handlerValidationError(MethodArgumentNotValidException ex) {
        Map<String, List<String>> fieldErr = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach((err)->
                        fieldErr.computeIfAbsent(err.getField(), k -> new ArrayList<>())
                                .add(err.getDefaultMessage())
                );

        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR.getDefaultMessage(), fieldErr);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, ErrorCode code, String message) {
        ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                code,
                message
        );
        return ResponseEntity
                .status(status)
                .body(err);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, ErrorCode code, String message, Map<String, List<String>> errs) {
        ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                code,
                message,
                errs
        );
        return ResponseEntity
                .status(status)
                .body(err);
    }
}
