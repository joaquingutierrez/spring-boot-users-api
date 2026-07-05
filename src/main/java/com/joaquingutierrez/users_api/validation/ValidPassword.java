package com.joaquingutierrez.users_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PasswordValidator.class})
@Documented
public @interface ValidPassword {
    String message() default "Contraseña inválida";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
