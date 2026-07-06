package com.joaquingutierrez.users_api.exceptions;

import com.joaquingutierrez.users_api.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistException extends ApiException {

    public EmailAlreadyExistException() {
        super(HttpStatus.CONFLICT, ErrorCode.EMAIL_ALREADY_EXISTS);
    }
}
