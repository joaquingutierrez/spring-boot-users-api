package com.joaquingutierrez.users_api.mappers;

import com.joaquingutierrez.users_api.dtos.CreateUserRequest;
import com.joaquingutierrez.users_api.dtos.UpdateUserRequest;
import com.joaquingutierrez.users_api.dtos.UserResponse;
import com.joaquingutierrez.users_api.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setEmail(user.getEmail());
        resp.setName(user.getName());
        resp.setLastName(user.getLastName());
        resp.setRole(user.getRole());
        resp.setCreatedAt(user.getCreatedAt());
        return resp;
    }

    public User toEntity(CreateUserRequest req) {
        User user = new User ();
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setLastName(req.getLastName());
        user.setPassword(req.getPassword());
        return user;
    }

    public void updateEntity(UpdateUserRequest req, User user) {
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setLastName(req.getLastName());
    }
}
