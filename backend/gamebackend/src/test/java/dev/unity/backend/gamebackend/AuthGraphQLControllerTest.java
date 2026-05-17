package dev.unity.backend.gamebackend;


import dev.unity.backend.gamebackend.entity.User;
import dev.unity.backend.gamebackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dev.unity.backend.gamebackend.controllers.AuthGraphQLController;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthGraphQLControllerTest {

    private UserRepository userRepository;
    private AuthGraphQLController controller;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        controller = new AuthGraphQLController(userRepository);
    }

    @Test
    void usersShouldReturnAll() {
        User u1 = new User(); u1.setUsername("A");
        User u2 = new User(); u2.setUsername("B");
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        Iterable<User> result = controller.users();

        assertTrue(result.iterator().hasNext());
        verify(userRepository).findAll();
    }

    @Test
    void userByEmailShouldReturnUserIfExists() {
        User u = new User(); u.setEmail("test@test.com");
        when(userRepository.findByEmailIgnoreCase("test@test.com")).thenReturn(Optional.of(u));

        User result = controller.userByEmail("test@test.com");

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void userByEmailShouldReturnNullIfNotExists() {
        when(userRepository.findByEmailIgnoreCase("missing@test.com")).thenReturn(Optional.empty());

        User result = controller.userByEmail("missing@test.com");

        assertNull(result);
    }

    @Test
    void registerShouldSaveUser() {
        User saved = new User(); saved.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = controller.register("user", "email@test.com", "pass");

        assertEquals(1L, result.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void loginShouldReturnUserIfPasswordMatches() {
        User u = new User(); u.setEmail("email@test.com"); u.setPassword("pass");
        when(userRepository.findByEmailIgnoreCase("email@test.com")).thenReturn(Optional.of(u));

        User result = controller.login("email@test.com", "pass");

        assertNotNull(result);
        assertEquals("email@test.com", result.getEmail());
    }

    @Test
    void loginShouldReturnNullIfPasswordWrong() {
        User u = new User(); u.setEmail("email@test.com"); u.setPassword("correct");
        when(userRepository.findByEmailIgnoreCase("email@test.com")).thenReturn(Optional.of(u));

        User result = controller.login("email@test.com", "wrong");

        assertNull(result);
    }
}
