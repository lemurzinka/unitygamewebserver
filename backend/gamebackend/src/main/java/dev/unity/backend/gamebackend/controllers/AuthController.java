package dev.unity.backend.gamebackend.controllers;

import dev.unity.backend.gamebackend.dto.RegisterRequest;
import dev.unity.backend.gamebackend.dto.UserResponseDto;
import dev.unity.backend.gamebackend.entity.Skin;
import dev.unity.backend.gamebackend.entity.User;
import dev.unity.backend.gamebackend.entity.UserLogin;
import dev.unity.backend.gamebackend.repository.UserLoginRepository;
import dev.unity.backend.gamebackend.repository.UserRepository;
import dev.unity.backend.gamebackend.services.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import java.util.UUID;


import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {
    "http://localhost:3001",
    "https://unitygamewebserver.vercel.app"
})

public class AuthController {

    @Value("${client-id}")
    private String googleClientId;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final UserLoginRepository userLoginRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    logger.info("Attempt to register user with username={} and email={}", request.getUsername(), request.getEmail());

    if (userRepository.existsByUsername(request.getUsername())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "USERNAME_EXISTS", "message", "Username already exists"));
    }
    if (userRepository.existsByEmail(request.getEmail())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "EMAIL_EXISTS", "message", "Email already registered"));
    }


    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword())); 
    user.setEmail(request.getEmail());
    userRepository.save(user);

 
    UserLogin login = new UserLogin();
    login.setUser(user);
    login.setLoginDate(LocalDateTime.now());
    userLoginRepository.save(login);

    logger.info("User registered successfully: id={}, username={}", user.getId(), user.getUsername());

    String token = jwtService.generateToken(user);

   UserResponseDto response = new UserResponseDto();
response.setMessage("User registered successful");
response.setId(user.getId());
response.setEmail(user.getEmail());
response.setUsername(user.getUsername());
response.setBalance(user.getBalance());
response.setIsAdmin(user.getIsAdmin());

response.setSelectedSkinId(
    user.getSelectedSkin() != null ? user.getSelectedSkin().getSkinId().longValue() : null
);

response.setOwnedSkinIds(
    user.getOwnedSkins().stream()
        .map(s -> s.getSkinId().longValue()) 
        .toList()
);



response.setToken(token);
return ResponseEntity.ok(response);

}

  @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    String password = request.get("password");

    logger.info("Login attempt with email={}", email);

    User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
    if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "INVALID_CREDENTIALS", "message", "Invalid email or password"));
    }


    if (Boolean.TRUE.equals(user.getIsAdmin())) {
        user.setBalance(100000); 
        userRepository.save(user); 
    }

    UserLogin login = new UserLogin();
    login.setUser(user);
    login.setLoginDate(LocalDateTime.now());
    userLoginRepository.save(login);

    String token = jwtService.generateToken(user);

    logger.info("User logged in successfully: id={}, username={}", user.getId(), user.getUsername());

     UserResponseDto response = new UserResponseDto();
response.setMessage("User login successful");
response.setId(user.getId());
response.setEmail(user.getEmail());
response.setUsername(user.getUsername());
response.setBalance(user.getBalance());
response.setIsAdmin(user.getIsAdmin());

response.setSelectedSkinId(
    user.getSelectedSkin() != null ? user.getSelectedSkin().getSkinId().longValue() : null
);

response.setOwnedSkinIds(
    user.getOwnedSkins().stream()
        .map(s -> s.getSkinId().longValue()) 
        .toList()
);



response.setToken(token);
return ResponseEntity.ok(response);
}

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        String idTokenString = request.get("idToken");

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance()
            )
            .setAudience(Collections.singletonList(googleClientId)) 
            .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
    GoogleIdToken.Payload payload = idToken.getPayload();
    String email = payload.getEmail();
    String name = (String) payload.get("name");

    User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
    if (user == null) {
        user = new User();
        user.setEmail(email);
        user.setUsername(name);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); 
        userRepository.save(user);
    }

    
    UserLogin login = new UserLogin();
    login.setUser(user);
    login.setLoginDate(LocalDateTime.now());
    userLoginRepository.save(login);

    String token = jwtService.generateToken(user);

     UserResponseDto response = new UserResponseDto();
response.setMessage("Google login successful");
response.setId(user.getId());
response.setEmail(user.getEmail());
response.setUsername(user.getUsername());
response.setBalance(user.getBalance());
response.setIsAdmin(user.getIsAdmin());

response.setSelectedSkinId(
    user.getSelectedSkin() != null ? user.getSelectedSkin().getSkinId().longValue() : null
);

response.setOwnedSkinIds(
    user.getOwnedSkins().stream()
        .map(s -> s.getSkinId().longValue()) 
        .toList()
);



response.setToken(token);
return ResponseEntity.ok(response);
}
 else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "INVALID_GOOGLE_TOKEN", "message", "Invalid Google ID token"));
        }
    } catch (Exception e) {
        logger.error("Google login error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "GOOGLE_AUTH_ERROR", "message", "Error verifying Google token"));
    }
}



}
