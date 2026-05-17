package dev.unity.backend.gamebackend;

import dev.unity.backend.gamebackend.dto.RegisterRequest;
import dev.unity.backend.gamebackend.entity.User;
import dev.unity.backend.gamebackend.repository.UserLoginRepository;
import dev.unity.backend.gamebackend.repository.UserRepository;
import dev.unity.backend.gamebackend.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import dev.unity.backend.gamebackend.controllers.AuthController;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private UserRepository userRepository;
    private UserLoginRepository userLoginRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userLoginRepository = mock(UserLoginRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);

        authController = new AuthController(userRepository, userLoginRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerShouldReturnConflictIfUsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user");
        request.setPassword("pass");
        request.setEmail("email@test.com");

        when(userRepository.existsByUsername("user")).thenReturn(true);

        ResponseEntity<?> response = authController.register(request);

        assertEquals(409, response.getStatusCodeValue());
        Map<?,?> body = (Map<?,?>) response.getBody();
        assertEquals("USERNAME_EXISTS", body.get("error"));
    }

@Test
void registerShouldSaveUserAndReturnToken() {
    RegisterRequest request = new RegisterRequest();
    request.setUsername("user");
    request.setPassword("pass");
    request.setEmail("email@test.com");

    when(userRepository.existsByUsername("user")).thenReturn(false);
    when(userRepository.existsByEmail("email@test.com")).thenReturn(false);
    when(passwordEncoder.encode("pass")).thenReturn("encoded");
    when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

    
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
        User u = invocation.getArgument(0);
        u.setId(1L);
        u.setBalance(0);
        u.setIsAdmin(false);
        return u;
    });

    ResponseEntity<?> response = authController.register(request);

    assertEquals(200, response.getStatusCodeValue());
    Map<?,?> body = (Map<?,?>) response.getBody();
    assertEquals("User registered successfully", body.get("message"));
    assertEquals("jwt-token", body.get("token"));

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    assertEquals("encoded", userCaptor.getValue().getPassword());
}


    @Test
    void loginShouldReturnUnauthorizedIfUserNotFound() {
        when(userRepository.findByEmailIgnoreCase("email@test.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(Map.of("email", "email@test.com", "password", "pass"));

        assertEquals(401, response.getStatusCodeValue());
        Map<?,?> body = (Map<?,?>) response.getBody();
        assertEquals("INVALID_CREDENTIALS", body.get("error"));
    }
@Test
void loginShouldReturnOkIfCredentialsValid() {
    User user = new User();
    user.setId(1L);              
    user.setEmail("email@test.com");
    user.setPassword("encoded");
    user.setUsername("user");
    user.setBalance(0);          
    user.setIsAdmin(false);    

    when(userRepository.findByEmailIgnoreCase("email@test.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("pass", "encoded")).thenReturn(true);
    when(jwtService.generateToken(user)).thenReturn("jwt-token");

    ResponseEntity<?> response = authController.login(Map.of("email", "email@test.com", "password", "pass"));

    assertEquals(200, response.getStatusCodeValue());
    Map<?,?> body = (Map<?,?>) response.getBody();
    assertEquals("User logged in successfully", body.get("message"));
    assertEquals("jwt-token", body.get("token"));
}

}
