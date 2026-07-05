package com.joaquingutierrez.users_api.services;

import com.joaquingutierrez.users_api.dtos.CreateUserRequest;
import com.joaquingutierrez.users_api.dtos.UpdateUserRequest;
import com.joaquingutierrez.users_api.dtos.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(CreateUserRequest user);
    UserResponse update(Long id, UpdateUserRequest user);
    void delete(Long id);
    UserResponse findById(Long id);
    UserResponse findByEmail(String email);
    List<UserResponse> findAll();
}
