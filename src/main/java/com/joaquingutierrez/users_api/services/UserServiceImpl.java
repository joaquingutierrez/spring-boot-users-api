package com.joaquingutierrez.users_api.services;

import com.joaquingutierrez.users_api.dtos.ChangePasswordRequest;
import com.joaquingutierrez.users_api.dtos.CreateUserRequest;
import com.joaquingutierrez.users_api.dtos.UpdateUserRequest;
import com.joaquingutierrez.users_api.dtos.UserResponse;
import com.joaquingutierrez.users_api.entities.User;
import com.joaquingutierrez.users_api.enums.Role;
import com.joaquingutierrez.users_api.exceptions.EmailAlreadyExistException;
import com.joaquingutierrez.users_api.exceptions.IncorrectPasswordException;
import com.joaquingutierrez.users_api.exceptions.NewPasswordMustBeDifferentException;
import com.joaquingutierrez.users_api.exceptions.UserNotFoundException;
import com.joaquingutierrez.users_api.mappers.UserMapper;
import com.joaquingutierrez.users_api.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse create(CreateUserRequest req) {
        if(userRepository.existsByEmail(req.getEmail())){
            throw new EmailAlreadyExistException();
        }

        User user = userMapper.toEntity(req);
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest req) {
        User existingUser = getUser(id);
        if (userRepository.existsByEmailAndIdNot(req.getEmail(), id)) {
            throw new EmailAlreadyExistException();
        }
        userMapper.updateEntity(req, existingUser);

        return userMapper.toResponse(userRepository.save(existingUser));
    }

    @Override
    public void delete(Long id) {
        getUser(id);
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse findById(Long id) {
        return userMapper.toResponse(getUser(id));
    }

    @Override
    public UserResponse findByEmail(String email) {
        return userMapper.toResponse(userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new));
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public void changePassword(Long id, ChangePasswordRequest req) {
        User user = getUser(id);

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }

        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw new NewPasswordMustBeDifferentException();
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }
}