package dev.unity.backend.gamebackend.controllers;

import dev.unity.backend.gamebackend.dto.RegisterRequest;
import dev.unity.backend.gamebackend.entity.User;
import dev.unity.backend.gamebackend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3001") 
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Attempt to register user with username={} and email={}", request.getUsername(), request.getEmail());

        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Registration failed: username {} already exists", request.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "USERNAME_EXISTS", "message", "Username already exists"));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: email {} already exists", request.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "EMAIL_EXISTS", "message", "Email already registered. Please sign in."));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); 
        user.setEmail(request.getEmail());

        userRepository.save(user);
        logger.info("User registered successfully: id={}, username={}", user.getId(), user.getUsername());

        return ResponseEntity.ok(Map.of(
            "message", "User registered successfully",
            "userId", user.getId(),
            "email", user.getEmail(),
            "username", user.getUsername()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        logger.info("Login attempt with email={}", email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            logger.warn("Login failed: user with email={} not found", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "USER_NOT_FOUND", "message", "User not found"));
        }

        if (!user.getPassword().equals(password)) {
            logger.warn("Login failed: invalid password for email={}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "INVALID_PASSWORD", "message", "Invalid password"));
        }

        logger.info("User logged in successfully: id={}, username={}", user.getId(), user.getUsername());

        return ResponseEntity.ok(Map.of(
            "message", "Sign in successful",
            "userId", user.getId(),
            "email", user.getEmail(),
            "username", user.getUsername()
        ));
    }
}
