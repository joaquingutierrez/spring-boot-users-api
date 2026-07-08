package com.joaquingutierrez.users_api.exceptions;

import com.joaquingutierrez.users_api.dtos.ErrorResponse;
import com.joaquingutierrez.users_api.enums.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnErrorResponseWhenApiExceptionIsThrown() {
        ApiException userNotFoundEx = new UserNotFoundException();

        ResponseEntity<ErrorResponse> response = handler.handlerException(userNotFoundEx);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ErrorCode.USER_NOT_FOUND, response.getBody().getCode());
        assertEquals(ErrorCode.USER_NOT_FOUND.getDefaultMessage(), response.getBody().getMessage());
        assertTrue(response.getBody().getErrors().isEmpty());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void shouldReturnValidationErrorsWhenMethodArgumentNotValidExceptionIsThrown() {
        FieldError errorEmail = new FieldError("user", "email", "Email inválido");
        FieldError errorPass1 = new FieldError("user", "password", "Debe ser de al menos 8 caracteres");
        FieldError errorPass2 = new FieldError("user", "password", "Debe contener al menos un numero");

        List<FieldError> fieldErr = List.of(errorEmail, errorPass1, errorPass2);

        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErr);
        ResponseEntity<ErrorResponse> response = handler.handlerValidationError(validationException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse body = response.getBody();

        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals(ErrorCode.VALIDATION_ERROR, body.getCode());
        assertEquals(ErrorCode.VALIDATION_ERROR.getDefaultMessage(), body.getMessage());

        assertEquals(List.of("Email inválido"), body.getErrors().get("email"));
        assertEquals(List.of("Debe ser de al menos 8 caracteres", "Debe contener al menos un numero"), body.getErrors().get("password"));

        assertNotNull(body.getTimestamp());
    }
}
