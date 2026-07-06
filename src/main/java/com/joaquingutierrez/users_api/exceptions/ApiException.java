package com.joaquingutierrez.users_api.exceptions;

import com.joaquingutierrez.users_api.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;

    public ApiException(HttpStatus httpStatus, ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
