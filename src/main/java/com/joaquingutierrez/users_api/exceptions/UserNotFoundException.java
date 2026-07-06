package com.joaquingutierrez.users_api.exceptions;

import com.joaquingutierrez.users_api.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }
}
