package com.joaquingutierrez.users_api.testdata;

import com.joaquingutierrez.users_api.dtos.ChangePasswordRequest;
import com.joaquingutierrez.users_api.dtos.CreateUserRequest;
import com.joaquingutierrez.users_api.dtos.UpdateUserRequest;
import com.joaquingutierrez.users_api.dtos.UserResponse;
import com.joaquingutierrez.users_api.entities.User;
import com.joaquingutierrez.users_api.enums.Role;

import java.time.LocalDateTime;

public final class UserTestData {

    private static final Long DEFAULT_ID = 1L;
    private static final String DEFAULT_EMAIL = "joaquin@test.com";
    private static final String DEFAULT_PASSWORD = "Hola1234!";
    private static final String DEFAULT_NAME = "Joaquin";
    private static final String DEFAULT_LASTNAME = "Gutierrez";
    private static final Role DEFAULT_ROLE = Role.ROLE_USER;
    private static final LocalDateTime DEFAULT_CREATED_AT =
            LocalDateTime.of(2026, 7, 6, 10, 0);
    private static final String DEFAULT_NEW_PASSWORD = "Hola9876!";
    private UserTestData() {
    }

    public static User defaultUser() {
        User user = new User();

        user.setId(DEFAULT_ID);
        user.setEmail(DEFAULT_EMAIL);
        user.setPassword(DEFAULT_PASSWORD);
        user.setName(DEFAULT_NAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setCreatedAt(DEFAULT_CREATED_AT);
        user.setRole(DEFAULT_ROLE);

        return user;
    }

    public static UserResponse defaultUserResponse() {
        User user = defaultUser();

        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setEmail(user.getEmail());
        resp.setName(user.getName());
        resp.setLastName(user.getLastName());
        resp.setCreatedAt(user.getCreatedAt());
        resp.setRole(user.getRole());

        return resp;
    }

    public static CreateUserRequest defaultCreateUserRequest() {
        User user = defaultUser();

        CreateUserRequest req = new CreateUserRequest();
        req.setEmail(user.getEmail());
        req.setPassword(user.getPassword());
        req.setName(user.getName());
        req.setLastName(user.getLastName());

        return req;
    }

    public static UpdateUserRequest defaultUpdateUserRequest() {
        User user = defaultUser();

        UpdateUserRequest req = new UpdateUserRequest();
        req.setEmail("lucasperez@test.com");
        req.setName("Lucas");
        req.setLastName("Perez");

        return req;
    }

    public static ChangePasswordRequest defaultChangePassword() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword(DEFAULT_PASSWORD);
        req.setNewPassword(DEFAULT_NEW_PASSWORD);
        return req;
    }
}
