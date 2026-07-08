package com.joaquingutierrez.users_api.controllers;

import com.joaquingutierrez.users_api.dtos.ChangePasswordRequest;
import com.joaquingutierrez.users_api.dtos.CreateUserRequest;
import com.joaquingutierrez.users_api.dtos.UpdateUserRequest;
import com.joaquingutierrez.users_api.dtos.UserResponse;
import com.joaquingutierrez.users_api.enums.ErrorCode;
import com.joaquingutierrez.users_api.exceptions.EmailAlreadyExistException;
import com.joaquingutierrez.users_api.exceptions.IncorrectPasswordException;
import com.joaquingutierrez.users_api.exceptions.NewPasswordMustBeDifferentException;
import com.joaquingutierrez.users_api.exceptions.UserNotFoundException;
import com.joaquingutierrez.users_api.services.UserService;
import com.joaquingutierrez.users_api.testdata.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser
    void shouldReturnUserWhenIdExists() throws Exception {
        UserResponse response = UserTestData.defaultUserResponse();

        when(userService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.email").value(response.getEmail()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.role").value(response.getRole().name()));

        verify(userService).findById(1L);
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenUserIdDoesNotExist() throws Exception {
        when(userService.findById(99L)).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(userService).findById(99L);
    }

    @Test
    @WithMockUser
    void shouldReturnUserWhenEmailExists() throws Exception {
        UserResponse response = UserTestData.defaultUserResponse();
        String email = response.getEmail();

        when(userService.findByEmail(email)).thenReturn(response);

        mockMvc.perform(get("/users/search")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.email").value(response.getEmail()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.role").value(response.getRole().name()));

        verify(userService).findByEmail(email);
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenEmailDoesNotExist() throws Exception {
        String email = "no_existe@test.com";

        when(userService.findByEmail(email)).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/users/search")
                        .param("email", email))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(userService).findByEmail(email);
    }

    @Test
    @WithMockUser
    void shouldReturnAllUsersWhenUsersExist() throws Exception {
        UserResponse resp1 = UserTestData.defaultUserResponse();
        UserResponse resp2 = UserTestData.defaultUserResponse();
        resp2.setId(2L);
        resp2.setEmail("prueba2@test.com");
        UserResponse resp3 = UserTestData.defaultUserResponse();
        resp3.setId(3L);
        resp3.setEmail("prueba3@test.com");

        when(userService.findAll()).thenReturn(List.of(resp1,resp2,resp3));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(resp1.getId()))
                .andExpect(jsonPath("$[0].email").value(resp1.getEmail()))
                .andExpect(jsonPath("$[1].id").value(resp2.getId()))
                .andExpect(jsonPath("$[1].email").value(resp2.getEmail()))
                .andExpect(jsonPath("$[2].id").value(resp3.getId()))
                .andExpect(jsonPath("$[2].email").value(resp3.getEmail()));

        verify(userService).findAll();
    }

    @Test
    @WithMockUser
    void shouldReturnEmptyListWhenNoUsersExist() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService).findAll();
    }

    @Test
    @WithMockUser
    void shouldCreateUserWhenRequestIsValid() throws Exception {
        CreateUserRequest req = UserTestData.defaultCreateUserRequest();
        UserResponse resp = UserTestData.defaultUserResponse();

        when(userService.create(any(CreateUserRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(resp.getId()))
                .andExpect(jsonPath("$.email").value(resp.getEmail()))
                .andExpect(jsonPath("$.name").value(resp.getName()))
                .andExpect(jsonPath("$.lastName").value(resp.getLastName()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.role").value(resp.getRole().name()));

        verify(userService).create(any(CreateUserRequest.class));
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenCreateUserRequestIsInvalid() throws Exception {
        CreateUserRequest req = UserTestData.defaultCreateUserRequest();
        req.setEmail("");

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.VALIDATION_ERROR.getDefaultMessage()))
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(userService, never()).create(any(CreateUserRequest.class));
    }

    @Test
    @WithMockUser
    void shouldReturnConflictFromCreateUserRequestWhenEmailAlreadyExists() throws Exception {
        CreateUserRequest req = UserTestData.defaultCreateUserRequest();

        when(userService.create(any(CreateUserRequest.class))).thenThrow(new EmailAlreadyExistException());

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.EMAIL_ALREADY_EXISTS.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.EMAIL_ALREADY_EXISTS.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(userService).create(any(CreateUserRequest.class));
    }

    @Test
    @WithMockUser
    void shouldUpdateUserWhenUserExistsAndRequestIsValid() throws Exception {
        UpdateUserRequest req = UserTestData.defaultUpdateUserRequest();
        UserResponse resp = UserTestData.defaultUserResponse();
        Long id = resp.getId();

        when(userService.update(any(Long.class), any(UpdateUserRequest.class))).thenReturn(resp);

        mockMvc.perform(put("/users/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(resp.getId()))
                .andExpect(jsonPath("$.email").value(resp.getEmail()))
                .andExpect(jsonPath("$.name").value(resp.getName()))
                .andExpect(jsonPath("$.lastName").value(resp.getLastName()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.role").value(resp.getRole().name()));

        verify(userService).update(any(Long.class), any(UpdateUserRequest.class));
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenUpdateRequestIsInvalid() throws Exception {
        UpdateUserRequest req = UserTestData.defaultUpdateUserRequest();
        req.setEmail("");

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.VALIDATION_ERROR.getDefaultMessage()))
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(userService, never()).update(any(Long.class), any(UpdateUserRequest.class));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenUpdatingNonExistingUser() throws Exception {
        UpdateUserRequest req = UserTestData.defaultUpdateUserRequest();
        Long id = 99L;

        when(userService.update(any(Long.class), any(UpdateUserRequest.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(put("/users/{id}", id)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(userService).update(any(Long.class), any(UpdateUserRequest.class));
    }

    @Test
    @WithMockUser
    void shouldReturnConflictWhenUpdatingWithExistingEmail() throws Exception {
        UpdateUserRequest req = UserTestData.defaultUpdateUserRequest();
        Long id = 1L;

        when(userService.update(any(Long.class), any(UpdateUserRequest.class))).thenThrow(new EmailAlreadyExistException());

        mockMvc.perform(put("/users/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.EMAIL_ALREADY_EXISTS.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.EMAIL_ALREADY_EXISTS.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(userService).update(any(Long.class), any(UpdateUserRequest.class));
    }

    @Test
    @WithMockUser
    void shouldDeleteUserWhenIdExists() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/users/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).delete(id);
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenDeletingNonExistingUser() throws Exception {
        Long id = 99L;

        doThrow(new UserNotFoundException())
                .when(userService).delete(id);

        mockMvc.perform(delete("/users/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(userService).delete(id);
    }

    @Test
    @WithMockUser
    void shouldChangePasswordWhenRequestIsValid() throws Exception {
        ChangePasswordRequest req = UserTestData.defaultChangePassword();
        Long id = 1L;

        mockMvc.perform(patch("/users/{id}/change-password", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        verify(userService).changePassword(any(Long.class), any(ChangePasswordRequest.class));
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenChangePasswordRequestIsInvalid() throws Exception {
        ChangePasswordRequest req = UserTestData.defaultChangePassword();
        req.setNewPassword("as");
        Long id = 1L;

        mockMvc.perform(patch("/users/{id}/change-password", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.VALIDATION_ERROR.getDefaultMessage()))
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(userService, never()).update(any(Long.class), any(UpdateUserRequest.class));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenChangingPasswordForNonExistingUser() throws Exception {
        Long id = 99L;
        ChangePasswordRequest req = UserTestData.defaultChangePassword();

        doThrow(new UserNotFoundException())
                .when(userService).changePassword(any(Long.class), any(ChangePasswordRequest.class));

        mockMvc.perform(patch("/users/{id}/change-password", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(userService).changePassword(any(Long.class), any(ChangePasswordRequest.class));
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenOldPasswordIsIncorrect() throws Exception {
        Long id = 99L;
        ChangePasswordRequest req = UserTestData.defaultChangePassword();

        doThrow(new IncorrectPasswordException())
                .when(userService).changePassword(any(Long.class), any(ChangePasswordRequest.class));

        mockMvc.perform(patch("/users/{id}/change-password", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.INCORRECT_PASSWORD.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INCORRECT_PASSWORD.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(userService).changePassword(any(Long.class), any(ChangePasswordRequest.class));
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenNewPasswordMatchesCurrentPassword() throws Exception {
        Long id = 99L;
        ChangePasswordRequest req = UserTestData.defaultChangePassword();
        req.setNewPassword(req.getOldPassword());

        doThrow(new NewPasswordMustBeDifferentException())
                .when(userService).changePassword(any(Long.class), any(ChangePasswordRequest.class));

        mockMvc.perform(patch("/users/{id}/change-password", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(userService).changePassword(any(Long.class), any(ChangePasswordRequest.class));
    }
}