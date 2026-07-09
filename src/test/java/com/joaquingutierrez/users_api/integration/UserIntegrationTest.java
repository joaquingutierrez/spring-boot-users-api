package com.joaquingutierrez.users_api.integration;

import com.joaquingutierrez.users_api.dtos.ChangePasswordRequest;
import com.joaquingutierrez.users_api.dtos.CreateUserRequest;
import com.joaquingutierrez.users_api.dtos.UpdateUserRequest;
import com.joaquingutierrez.users_api.entities.User;
import com.joaquingutierrez.users_api.enums.ErrorCode;
import com.joaquingutierrez.users_api.enums.Role;
import com.joaquingutierrez.users_api.repositories.UserRepository;
import com.joaquingutierrez.users_api.testdata.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Test
    @WithMockUser
    void shouldReturnUserWhenIdExists() throws Exception {
        User user = UserTestData.defaultUser();
        user.setId(null);
        User actual = userRepository.save(user);

        mockMvc.perform(get("/users/{id}", actual.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.role").value(user.getRole().name()));
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenUserDoesNotExist() throws Exception {
        User user = UserTestData.defaultUser();
        user.setId(null);
        Long id = 99L;
        User actual = userRepository.save(user);

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @WithMockUser
    void shouldCreateUser() throws Exception {
        CreateUserRequest user = UserTestData.defaultCreateUserRequest();

        mockMvc.perform(post("/users")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.role").value(Role.ROLE_USER.name()));
    }

    @Test
    @WithMockUser
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        CreateUserRequest user = UserTestData.defaultCreateUserRequest();
        user.setEmail("");

        mockMvc.perform(post("/users")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.VALIDATION_ERROR.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @WithMockUser
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        User user1 = UserTestData.defaultUser();
        user1.setId(null);
        CreateUserRequest user2 = UserTestData.defaultCreateUserRequest();
        user1.setEmail("joaco@test.com");
        user2.setEmail("joaco@test.com");

        userRepository.save(user1);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.EMAIL_ALREADY_EXISTS.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.EMAIL_ALREADY_EXISTS.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @WithMockUser
    void shouldUpdateUser() throws Exception {
        UpdateUserRequest req = UserTestData.defaultUpdateUserRequest();
        User user = UserTestData.defaultUser();
        user.setId(null);

        user = userRepository.save(user);

        mockMvc.perform(put("/users/{id}", user.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(req.getName()))
                .andExpect(jsonPath("$.lastName").value(req.getLastName()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.role").value(Role.ROLE_USER.name()));
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenUpdatingNonExistingUser() throws Exception {
        UpdateUserRequest req = UserTestData.defaultUpdateUserRequest();
        User user = UserTestData.defaultUser();
        user.setId(null);

        user = userRepository.save(user);

        mockMvc.perform(put("/users/{id}", 99L)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @WithMockUser
    void shouldDeleteUser() throws Exception {
        User user = UserTestData.defaultUser();
        user.setId(null);
        user = userRepository.save(user);

        mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenDeletingNonExistingUser() throws Exception {
        User user = UserTestData.defaultUser();
        user.setId(null);
        user = userRepository.save(user);

        mockMvc.perform(delete("/users/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @WithMockUser
    void shouldChangePassword() throws Exception {
        ChangePasswordRequest req = UserTestData.defaultChangePassword();
        User user = UserTestData.defaultUser();
        user.setId(null);
        user.setPassword(encoder.encode(user.getPassword()));
        user = userRepository.save(user);

        mockMvc.perform(patch("/users/{id}/change-password", user.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void shouldReturn400WhenOldPasswordIsIncorrect() throws Exception {
        ChangePasswordRequest req = UserTestData.defaultChangePassword();
        req.setOldPassword("OtraContraseñ124!");
        User user = UserTestData.defaultUser();
        user.setId(null);
        user.setPassword(encoder.encode(user.getPassword()));

        user = userRepository.save(user);

        mockMvc.perform(patch("/users/{id}/change-password", user.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.INCORRECT_PASSWORD.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INCORRECT_PASSWORD.getDefaultMessage()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
