package com.joaquingutierrez.users_api.repositories;

import com.joaquingutierrez.users_api.entities.User;
import com.joaquingutierrez.users_api.testdata.UserTestData;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnUserWhenEmailExists() {
        User user = UserTestData.defaultUser();
        user.setId(null);

        userRepository.save(user);

        Optional<User> actual = userRepository.findByEmail(user.getEmail());

        assertTrue(actual.isPresent());
        assertEquals(user.getEmail(), actual.get().getEmail());
        assertEquals(user.getName(), actual.get().getName());
    }

    @Test
    void shouldReturnEmptyOptionalWhenEmailDoesNotExist() {
        String email = "alguncorreo@test.com";

        Optional<User> actual = userRepository.findByEmail(email);

        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        User user = UserTestData.defaultUser();
        user.setId(null);

        userRepository.save(user);

        boolean actual = userRepository.existsByEmail(user.getEmail());

        assertTrue(actual);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        String email = "alguncorreo@test.com";

        boolean actual = userRepository.existsByEmail(email);

        assertFalse(actual);
    }

    @Test
    void shouldReturnTrueWhenAnotherUserHasSameEmail() {
        User user1 = UserTestData.defaultUser();
        user1.setId(null);
        user1.setEmail("joaquin@test.com");

        User user2 = UserTestData.defaultUser();
        user2.setId(null);
        user2.setEmail("otrocorreodistinto@test.com");

        userRepository.save(user1);
        user2 = userRepository.save(user2);


        boolean actual = userRepository.existsByEmailAndIdNot(user1.getEmail(), user2.getId());

        assertTrue(actual);
    }

    @Test
    void shouldReturnFalseWhenOnlyCurrentUserHasEmail() {
        User user1 = UserTestData.defaultUser();
        user1.setId(null);
        user1.setEmail("joaquin@test.com");

        User user2 = UserTestData.defaultUser();
        user2.setId(null);
        user2.setEmail("otrocorreodistinto@test.com");

        userRepository.save(user1);
        user2 = userRepository.save(user2);


        boolean actual = userRepository.existsByEmailAndIdNot(user2.getEmail(), user2.getId());

        assertFalse(actual);
    }
}
