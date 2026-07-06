package com.joaquingutierrez.users_api.exceptions;

import com.joaquingutierrez.users_api.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class NewPasswordMustBeDifferentException extends ApiException {

    public NewPasswordMustBeDifferentException() {
        super(HttpStatus.BAD_REQUEST, ErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT);
    }
}
