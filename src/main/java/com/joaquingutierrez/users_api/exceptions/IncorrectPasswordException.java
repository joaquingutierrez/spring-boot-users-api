package com.joaquingutierrez.users_api.exceptions;

import com.joaquingutierrez.users_api.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class IncorrectPasswordException extends ApiException {
    public IncorrectPasswordException() {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INCORRECT_PASSWORD);
    }
}
