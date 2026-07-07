package com.joaquingutierrez.users_api.services;

import com.joaquingutierrez.users_api.dtos.ChangePasswordRequest;
import com.joaquingutierrez.users_api.dtos.CreateUserRequest;
import com.joaquingutierrez.users_api.dtos.UpdateUserRequest;
import com.joaquingutierrez.users_api.dtos.UserResponse;
import com.joaquingutierrez.users_api.entities.User;
import com.joaquingutierrez.users_api.exceptions.EmailAlreadyExistException;
import com.joaquingutierrez.users_api.exceptions.IncorrectPasswordException;
import com.joaquingutierrez.users_api.exceptions.NewPasswordMustBeDifferentException;
import com.joaquingutierrez.users_api.exceptions.UserNotFoundException;
import com.joaquingutierrez.users_api.mappers.UserMapper;
import com.joaquingutierrez.users_api.repositories.UserRepository;
import com.joaquingutierrez.users_api.testdata.UserTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void shouldReturnUserWhenIdExists() {
        User user = UserTestData.defaultUser();
        Long id = user.getId();
        UserResponse expected = UserTestData.defaultUserResponse();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expected);

        UserResponse actual = userServiceImpl.findById(id);


        assertEquals(expected, actual);

        verify(userRepository).findById(id);
        verify(userMapper).toResponse(user);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenIdDoesNotExist() {
        Long id = UserTestData.defaultUser().getId();

        when(userRepository.findById(id)).thenReturn(Optional.empty());


        assertThrows(UserNotFoundException.class, () -> userServiceImpl.findById(id));

        verify(userRepository).findById(id);
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    void shouldReturnUserWhenEmailExists() {
        User user = UserTestData.defaultUser();
        UserResponse expected = UserTestData.defaultUserResponse();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expected);


        UserResponse actual = userServiceImpl.findByEmail(user.getEmail());


        assertEquals(expected, actual);

        verify(userRepository).findByEmail(user.getEmail());
        verify(userMapper).toResponse(user);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenEmailDoesNotExist() {
        String email = UserTestData.defaultUser().getEmail();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());


        assertThrows(UserNotFoundException.class, () -> userServiceImpl.findByEmail(email));

        verify(userRepository).findByEmail(email);
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    void shouldCreateUserWhenEmailDoesNotExist() {
        CreateUserRequest req = UserTestData.defaultCreateUserRequest();
        User user = UserTestData.defaultUser();
        UserResponse expected = UserTestData.defaultUserResponse();
        String originalPassword = req.getPassword();
        String encodedPassword = "$2a$10$hashedPassword";

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(userMapper.toEntity(req)).thenReturn(user);
        when(passwordEncoder.encode(originalPassword)).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expected);


        UserResponse actual = userServiceImpl.create(req);


        assertEquals(expected, actual);
        assertEquals(encodedPassword, user.getPassword());

        verify(userRepository).existsByEmail(req.getEmail());
        verify(userMapper).toEntity(req);
        verify(passwordEncoder).encode(originalPassword);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void shouldUpdateUserWhenIdExists() {
        UpdateUserRequest req = UserTestData.defaultUpdateUserRequest();
        User user = UserTestData.defaultUser();
        UserResponse expected = UserTestData.defaultUserResponse();
        Long id = user.getId();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(req.getEmail(), id)).thenReturn(false);
        doNothing().when(userMapper).updateEntity(req, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expected);


        UserResponse actual = userServiceImpl.update(id, req);


        assertEquals(expected, actual);

        verify(userRepository).findById(id);
        verify(userRepository).existsByEmailAndIdNot(req.getEmail(), id);
        verify(userMapper).updateEntity(req, user);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUpdatingNonExistingUser() {
        Long id = 1L;
        UpdateUserRequest req = UserTestData.defaultUpdateUserRequest();

        when(userRepository.findById(id)).thenReturn(Optional.empty());


        assertThrows(UserNotFoundException.class, () -> userServiceImpl.update(id, req));

        verify(userRepository).findById(id);
        verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
        verify(userMapper, never()).updateEntity(any(), any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    void shouldThrowEmailAlreadyExistExceptionWhenUpdatingWithExistingEmail() {
        Long id = 1L;
        UpdateUserRequest req = UserTestData.defaultUpdateUserRequest();
        User user = UserTestData.defaultUser();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(req.getEmail(), id)).thenReturn(true);


        assertThrows(EmailAlreadyExistException.class, () -> userServiceImpl.update(id, req));

        verify(userRepository).findById(id);
        verify(userRepository).existsByEmailAndIdNot(req.getEmail(), id);
        verify(userMapper, never()).updateEntity(any(), any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    void shouldChangePasswordWhenOldPasswordIsCorrect () {
        ChangePasswordRequest req = UserTestData.defaultChangePassword();
        Long id = 1L;
        User user = UserTestData.defaultUser();
        String originalPassword = user.getPassword();
        String encodedNewPassword = "$2a$10$hashedPassword";

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getOldPassword(), originalPassword)).thenReturn(true);
        when(passwordEncoder.matches(req.getNewPassword(), originalPassword)).thenReturn(false);
        when(passwordEncoder.encode(req.getNewPassword())).thenReturn(encodedNewPassword);


        userServiceImpl.changePassword(id, req);


        assertEquals(encodedNewPassword, user.getPassword());

        verify(userRepository).findById(id);
        verify(passwordEncoder).matches(req.getOldPassword(), originalPassword);
        verify(passwordEncoder).matches(req.getNewPassword(), originalPassword);
        verify(passwordEncoder).encode(req.getNewPassword());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        ChangePasswordRequest req = UserTestData.defaultChangePassword();
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());


        assertThrows(UserNotFoundException.class, () -> userServiceImpl.changePassword(id, req));

        verify(userRepository).findById(id);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldThrowIncorrectPasswordExceptionWhenOldPasswordIsIncorrect() {
        ChangePasswordRequest req = UserTestData.defaultChangePassword();
        Long id = 1L;
        User user = UserTestData.defaultUser();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getOldPassword(), user.getPassword())).thenReturn(false);


        assertThrows(IncorrectPasswordException.class, () -> userServiceImpl.changePassword(id, req));

        verify(userRepository).findById(id);
        verify(passwordEncoder).matches(req.getOldPassword(), user.getPassword());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldThrowNewPasswordMustBeDifferentExceptionWhenPasswordsAreEqual() {
        ChangePasswordRequest req = UserTestData.defaultChangePassword();
        Long id = 1L;
        User user = UserTestData.defaultUser();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getOldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(req.getNewPassword(), user.getPassword())).thenReturn(true);


        assertThrows(NewPasswordMustBeDifferentException.class, () -> userServiceImpl.changePassword(id, req));

        verify(userRepository).findById(id);
        verify(passwordEncoder).matches(req.getOldPassword(), user.getPassword());
        verify(passwordEncoder).matches(req.getNewPassword(), user.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldDeleteUserWhenIdExists() {
        User user = UserTestData.defaultUser();
        Long id = user.getId();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userServiceImpl.delete(id);

        verify(userRepository).findById(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenDeletingNonExistingUser() {
        Long id = UserTestData.defaultUser().getId();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->userServiceImpl.delete(id));

        verify(userRepository).findById(id);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldReturnAllUsers() {
        Long id2 = 2L;
        String email2 = "otro@test.com";

        User user1 = UserTestData.defaultUser();
        User user2 = UserTestData.defaultUser();
        user2.setId(id2);
        user2.setEmail(email2);

        UserResponse resp1 = UserTestData.defaultUserResponse();
        UserResponse resp2 = UserTestData.defaultUserResponse();
        resp2.setId(id2);
        resp2.setEmail(email2);

        List<UserResponse> expected = List.of(resp1, resp2);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toResponse(user1)).thenReturn(resp1);
        when(userMapper.toResponse(user2)).thenReturn(resp2);


        List<UserResponse> actual = userServiceImpl.findAll();


        assertEquals(expected, actual);

        verify(userRepository).findAll();
        verify(userMapper).toResponse(user1);
        verify(userMapper).toResponse(user2);
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        List<UserResponse> expected = List.of();

        when(userRepository.findAll()).thenReturn(List.of());


        List<UserResponse> actual = userServiceImpl.findAll();


        assertTrue(actual.isEmpty());

        verify(userRepository).findAll();
        verify(userMapper, never()).toResponse(any());
    }
}
