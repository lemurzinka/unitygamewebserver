package dev.unity.backend.gamebackend.controllers;

import dev.unity.backend.gamebackend.entity.Skin;
import dev.unity.backend.gamebackend.entity.User;
import dev.unity.backend.gamebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    @GetMapping("/{id}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        logger.info("📊 Returning balance for user {}: {}", user.getUsername(), user.getBalance());
        return ResponseEntity.ok(Map.of("balance", user.getBalance()));
    }

    @GetMapping("/me")
public ResponseEntity<?> getCurrentUser(Authentication auth) {
    String email = auth.getName();
    User user = userRepository.findByEmailWithSkins(email).orElseThrow();

    Map<String, Object> response = new HashMap<>();
    response.put("id", user.getId());
    response.put("balance", user.getBalance());
    response.put("ownedSkinIds", user.getOwnedSkins().stream().map(Skin::getSkinId).toList());
    response.put("selectedSkinId", user.getSelectedSkin() != null ? user.getSelectedSkin().getSkinId() : null);

    return ResponseEntity.ok(response);
}

}
