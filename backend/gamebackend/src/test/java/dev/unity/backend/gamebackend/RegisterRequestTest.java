package dev.unity.backend.gamebackend;

import dev.unity.backend.gamebackend.dto.RegisterRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailIfUsernameTooShort() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("ab"); 
        req.setEmail("test@test.com");
        req.setPassword("secret123");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 3 and 20")));
    }

    @Test
    void shouldFailIfEmailInvalid() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("validUser");
        req.setEmail("invalid-email");
        req.setPassword("secret123");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid email format")));
    }

    @Test
    void shouldFailIfPasswordTooShort() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("validUser");
        req.setEmail("test@test.com");
        req.setPassword("123"); // менше 6 символів

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("at least 6 characters")));
    }

    @Test
    void shouldPassIfAllFieldsValid() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("validUser");
        req.setEmail("test@test.com");
        req.setPassword("secret123");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }
}
