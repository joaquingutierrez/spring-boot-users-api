package com.joaquingutierrez.users_api.services;

import com.joaquingutierrez.users_api.dtos.CreateUserRequest;
import com.joaquingutierrez.users_api.dtos.UpdateUserRequest;
import com.joaquingutierrez.users_api.dtos.UserResponse;
import com.joaquingutierrez.users_api.entities.User;
import com.joaquingutierrez.users_api.enums.Role;
import com.joaquingutierrez.users_api.exceptions.EmailAlreadyExistException;
import com.joaquingutierrez.users_api.exceptions.UserNotFoundException;
import com.joaquingutierrez.users_api.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse create(CreateUserRequest req) {
        if(userRepository.existsByEmail(req.getEmail())){
            throw new EmailAlreadyExistException();
        }
        User user = map(req);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return map(userRepository.save(user));
    }

    private UserResponse map(User user) {
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setEmail(user.getEmail());
        resp.setName(user.getName());
        resp.setLastName(user.getLastName());
        resp.setRole(user.getRole());
        resp.setCreatedAt(user.getCreatedAt());
        return resp;
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest req) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        if (userRepository.existsByEmailAndIdNot(req.getEmail(), id)) {
            throw new EmailAlreadyExistException();
        }
        map(req, existingUser);

        return map(userRepository.save(existingUser));
    }

    private User map(CreateUserRequest req) {
        User user = new User ();
        map(req, user);
        user.setPassword(req.getPassword());
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.ROLE_USER);
        return user;
    }

    private void map(CreateUserRequest req, User user) {
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setLastName(req.getLastName());
    }

    private void map(UpdateUserRequest req, User user) {
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setLastName(req.getLastName());
    }

    @Override
    public void delete(Long id) {
        userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse findById(Long id) {
        return map(userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new));
    }

    @Override
    public UserResponse findByEmail(String email) {
        return map(userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new));
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }
}
