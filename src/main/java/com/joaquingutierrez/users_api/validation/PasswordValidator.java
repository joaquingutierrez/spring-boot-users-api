package com.joaquingutierrez.users_api.validation;

import com.joaquingutierrez.users_api.enums.ErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        context.disableDefaultConstraintViolation();

        if (password == null || password.isBlank()) {
            context.buildConstraintViolationWithTemplate(
                            ErrorCode.PASSWORD_EMPTY.getDefaultMessage())
                    .addConstraintViolation();
            return false;
        }

        if (password.length() < 8 || password.length() > 30) {
            context.buildConstraintViolationWithTemplate(
                            ErrorCode.PASSWORD_INVALID_LENGTH.getDefaultMessage())
                    .addConstraintViolation();
            return false;
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).*$")) {
            context.buildConstraintViolationWithTemplate(
                            ErrorCode.PASSWORD_INVALID_FORMAT.getDefaultMessage())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
