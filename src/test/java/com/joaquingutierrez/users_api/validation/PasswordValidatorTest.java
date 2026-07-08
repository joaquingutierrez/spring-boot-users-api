package com.joaquingutierrez.users_api.validation;

import com.joaquingutierrez.users_api.enums.ErrorCode;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Test
    void shouldAcceptValidPassword() {
        String validPassword = "Hola1234!";

        boolean isValidPassword = validator.isValid(validPassword, context);

        assertTrue(isValidPassword);
    }

    @Test
    void shouldRejectNullPassword() {
        String nullPassword = null;

        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);

        boolean isValidPassword = validator.isValid(nullPassword, context);

        assertFalse(isValidPassword);

        verify(context).buildConstraintViolationWithTemplate(
                        ErrorCode.PASSWORD_EMPTY.getDefaultMessage());
        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_LENGTH.getDefaultMessage());
        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_FORMAT.getDefaultMessage());
        verify(builder).addConstraintViolation();
    }

    @Test
    void shouldRejectEmptyPassword() {
        String emptyPassword = "";

        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);

        boolean isValidPassword = validator.isValid(emptyPassword, context);

        assertFalse(isValidPassword);

        verify(context).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_EMPTY.getDefaultMessage());
        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_LENGTH.getDefaultMessage());
        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_FORMAT.getDefaultMessage());
        verify(builder).addConstraintViolation();
    }

    @Test
    void shouldRejectPasswordShorterThanEightCharacters() {
        String shortPassword = "Hola2!";

        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);

        boolean isValidPassword = validator.isValid(shortPassword, context);

        assertFalse(isValidPassword);

        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_EMPTY.getDefaultMessage());
        verify(context).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_LENGTH.getDefaultMessage());
        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_FORMAT.getDefaultMessage());
        verify(builder).addConstraintViolation();
    }

    @Test
    void shouldRejectPasswordLongerThanThirtyCharacters() {
        String longPassword = "Hola2312313187491bfjdsbfJSKADk293414141v!";

        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);

        boolean isValidPassword = validator.isValid(longPassword, context);

        assertFalse(isValidPassword);

        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_EMPTY.getDefaultMessage());
        verify(context).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_LENGTH.getDefaultMessage());
        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_FORMAT.getDefaultMessage());
        verify(builder).addConstraintViolation();
    }

    @Test
    void shouldRejectPasswordWithoutUppercaseLetter() {
        String noUpperCaseLetterPassword = "hola1234!";

        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);

        boolean isValidPassword = validator.isValid(noUpperCaseLetterPassword, context);

        assertFalse(isValidPassword);

        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_EMPTY.getDefaultMessage());
        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_LENGTH.getDefaultMessage());
        verify(context).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_FORMAT.getDefaultMessage());
        verify(builder).addConstraintViolation();
    }

    @Test
    void shouldRejectPasswordWithoutLowercaseLetter() {
        String noLowerCaseLetterPassword = "HOLA1234!";

        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);

        boolean isValidPassword = validator.isValid(noLowerCaseLetterPassword, context);

        assertFalse(isValidPassword);

        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_EMPTY.getDefaultMessage());
        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_LENGTH.getDefaultMessage());
        verify(context).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_FORMAT.getDefaultMessage());
        verify(builder).addConstraintViolation();
    }

    @Test
    void shouldRejectPasswordWithoutNumber() {
        String noNumberPassword = "HolaHola!";

        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);

        boolean isValidPassword = validator.isValid(noNumberPassword, context);

        assertFalse(isValidPassword);

        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_EMPTY.getDefaultMessage());
        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_LENGTH.getDefaultMessage());
        verify(context).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_FORMAT.getDefaultMessage());
        verify(builder).addConstraintViolation();
    }

    @Test
    void shouldRejectPasswordWithoutSpecialCharacter() {
        String noSpecialCharacterPassword = "HolaHola1";

        when(context.buildConstraintViolationWithTemplate(any())).thenReturn(builder);

        boolean isValidPassword = validator.isValid(noSpecialCharacterPassword, context);

        assertFalse(isValidPassword);

        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_EMPTY.getDefaultMessage());
        verify(context, never()).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_LENGTH.getDefaultMessage());
        verify(context).buildConstraintViolationWithTemplate(
                ErrorCode.PASSWORD_INVALID_FORMAT.getDefaultMessage());
        verify(builder).addConstraintViolation();
    }
}
