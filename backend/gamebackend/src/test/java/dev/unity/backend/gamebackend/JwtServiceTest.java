package dev.unity.backend.gamebackend;

import dev.unity.backend.gamebackend.services.JwtService;

import dev.unity.backend.gamebackend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
      
        String secret = "this_is_a_very_long_secret_key_for_testing_123456";
        jwtService = new JwtService(secret);

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setUsername("tester");
    }

    @Test
    void generateTokenShouldContainEmail() {
        String token = jwtService.generateToken(user);
        String email = jwtService.extractUsername(token);

        assertEquals("test@test.com", email);
    }

    @Test
    void validateTokenShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(user);

        boolean valid = jwtService.validateToken(token, user);

        assertTrue(valid);
    }

    @Test
    void validateTokenShouldReturnFalseForWrongUser() {
        String token = jwtService.generateToken(user);

        User other = new User();
        other.setEmail("other@test.com");

        boolean valid = jwtService.validateToken(token, other);

        assertFalse(valid);
    }

 
}
