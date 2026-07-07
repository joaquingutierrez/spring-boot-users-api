package com.joaquingutierrez.users_api.mappers;

import com.joaquingutierrez.users_api.dtos.CreateUserRequest;
import com.joaquingutierrez.users_api.dtos.UpdateUserRequest;
import com.joaquingutierrez.users_api.dtos.UserResponse;
import com.joaquingutierrez.users_api.entities.User;
import com.joaquingutierrez.users_api.enums.Role;
import com.joaquingutierrez.users_api.testdata.UserTestData;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void shouldMapAllFieldsFromUserToUserResponse() {
        User user = UserTestData.defaultUser();

        UserResponse actual = userMapper.toResponse(user);

        assertEquals(user.getId(), actual.getId());
        assertEquals(user.getEmail(), actual.getEmail());
        assertEquals(user.getName(), actual.getName());
        assertEquals(user.getLastName(), actual.getLastName());
        assertEquals(user.getCreatedAt(), actual.getCreatedAt());
        assertEquals(user.getRole(), actual.getRole());
    }

    @Test
    void shouldMapAllFieldsFromCreateUserRequestToUser() {
        CreateUserRequest req = UserTestData.defaultCreateUserRequest();

        User actual = userMapper.toEntity(req);

        assertNull(actual.getId());
        assertEquals(req.getEmail(), actual.getEmail());
        assertEquals(req.getPassword(), actual.getPassword());
        assertEquals(req.getName(), actual.getName());
        assertEquals(req.getLastName(), actual.getLastName());
        assertNull(actual.getCreatedAt());
        assertNull(actual.getRole());
    }

    @Test
    void shouldUpdateUserAllFieldsFromUpdateUserRequest() {
        UpdateUserRequest req = UserTestData.defaultUpdateUserRequest();
        User user = UserTestData.defaultUser();
        Long originalId = user.getId();
        String originalPassword = user.getPassword();
        Role originalRole = user.getRole();
        LocalDateTime originalCreatedAt = user.getCreatedAt();

        userMapper.updateEntity(req, user);

        assertEquals(originalId, user.getId());
        assertEquals(req.getEmail(), user.getEmail());
        assertEquals(originalPassword, user.getPassword());
        assertEquals(req.getName(), user.getName());
        assertEquals(req.getLastName(), user.getLastName());
        assertEquals(originalCreatedAt, user.getCreatedAt());
        assertEquals(originalRole, user.getRole());
    }
}
