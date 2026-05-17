package dev.unity.backend.gamebackend;

import dev.unity.backend.gamebackend.controllers.SkinController;

import dev.unity.backend.gamebackend.entity.Skin;
import dev.unity.backend.gamebackend.entity.User;
import dev.unity.backend.gamebackend.repository.SkinRepository;
import dev.unity.backend.gamebackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SkinControllerTest {

    private SkinRepository skinRepository;
    private UserRepository userRepository;
    private SkinController controller;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        skinRepository = mock(SkinRepository.class);
        userRepository = mock(UserRepository.class);
        controller = new SkinController(skinRepository, userRepository);
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@test.com");
    }

    @Test
    void buySkinShouldReturnErrorIfUserOrSkinNotFound() {
        when(userRepository.findByEmailWithSkins("test@test.com")).thenReturn(Optional.empty());
        when(skinRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.buySkin(1, auth);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("USER_OR_SKIN_NOT_FOUND", ((java.util.Map<?,?>)response.getBody()).get("error"));
    }

    @Test
    void buySkinShouldReturnErrorIfInsufficientFunds() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setBalance(50);
        user.setOwnedSkins(new HashSet<>());

        Skin skin = new Skin();
        skin.setSkinId(1);
        skin.setPrice(100);

        when(userRepository.findByEmailWithSkins("test@test.com")).thenReturn(Optional.of(user));
        when(skinRepository.findById(1)).thenReturn(Optional.of(skin));

        ResponseEntity<?> response = controller.buySkin(1, auth);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("INSUFFICIENT_FUNDS", ((java.util.Map<?,?>)response.getBody()).get("error"));
    }

    @Test
    void buySkinShouldReturnSuccessIfPurchaseValid() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("tester");
        user.setBalance(200);
        user.setOwnedSkins(new HashSet<>());

        Skin skin = new Skin();
        skin.setSkinId(1);
        skin.setName("Cool Skin");
        skin.setPrice(100);

        when(userRepository.findByEmailWithSkins("test@test.com")).thenReturn(Optional.of(user));
        when(skinRepository.findById(1)).thenReturn(Optional.of(skin));
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = controller.buySkin(1, auth);

        assertEquals(200, response.getStatusCodeValue());
        java.util.Map<?,?> body = (java.util.Map<?,?>) response.getBody();
        assertEquals(true, body.get("success"));
        assertEquals(100, body.get("newBalance"));
        assertEquals(1, body.get("ownedSkinId"));

        assertTrue(user.getOwnedSkins().contains(skin));
    }
}
