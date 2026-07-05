package com.joaquingutierrez.users_api.enums;

public enum ErrorCode {
    USER_NOT_FOUND("Usuario no encontrado."),
    EMAIL_ALREADY_EXISTS("El email ya existe en la base de datos."),
    VALIDATION_ERROR("Error al validar los datos");

    private final String defaultMessage;

    ErrorCode (String message) {
        this.defaultMessage = message;
    }

    public String getDefaultMessage () {
        return this.defaultMessage;
    }
}
