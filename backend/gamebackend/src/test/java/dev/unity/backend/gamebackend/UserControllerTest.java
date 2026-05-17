package dev.unity.backend.gamebackend;

import dev.unity.backend.gamebackend.controllers.UserController;

import dev.unity.backend.gamebackend.entity.Skin;
import dev.unity.backend.gamebackend.entity.User;
import dev.unity.backend.gamebackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserRepository userRepository;
    private UserController controller;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        controller = new UserController(userRepository);
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@test.com");
    }

    @Test
    void getBalanceShouldReturnNotFoundIfUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Map<String,Object>> response = controller.getBalance(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void getBalanceShouldReturnBalanceIfUserExists() {
        User user = new User();
        user.setId(1L);
        user.setUsername("tester");
        user.setBalance(150);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<Map<String,Object>> response = controller.getBalance(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(150, response.getBody().get("balance"));
    }

    @Test
    void getCurrentUserShouldReturnUserDetails() {
        User user = new User();
        user.setId(1L);
        user.setBalance(200);

        Skin skin1 = new Skin(); skin1.setSkinId(10);
        Skin skin2 = new Skin(); skin2.setSkinId(20);
        user.setOwnedSkins(Set.of(skin1, skin2));
        user.setSelectedSkin(skin2);

        when(userRepository.findByEmailWithSkins("test@test.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = controller.getCurrentUser(auth);

        assertEquals(200, response.getStatusCodeValue());
        Map<?,?> body = (Map<?,?>) response.getBody();
        assertEquals(1L, body.get("id"));
        assertEquals(200, body.get("balance"));
        assertEquals(List.of(10,20), body.get("ownedSkinIds"));
        assertEquals(20, body.get("selectedSkinId"));
    }
}
