package com.joaquingutierrez.users_api.enums;

public enum ErrorCode {
    USER_NOT_FOUND("Usuario no encontrado."),
    EMAIL_ALREADY_EXISTS("El email ya existe en la base de datos."),
    VALIDATION_ERROR("Error al validar los datos"),
    INCORRECT_PASSWORD("Contraseña incorrecta."),
    PASSWORD_EMPTY("La contraseña no puede estar en blanco."),
    PASSWORD_INVALID_LENGTH("La contraseña debe tener entre 8 y 30 caracteres."),
    PASSWORD_INVALID_FORMAT("La contraseña debe contener una mayúscula, una minúscula, un número y un carácter especial."),
    NEW_PASSWORD_MUST_BE_DIFFERENT("La contraseña no puede ser la misma que la anterior.");

    private final String defaultMessage;

    ErrorCode (String message) {
        this.defaultMessage = message;
    }

    public String getDefaultMessage () {
        return this.defaultMessage;
    }
}
