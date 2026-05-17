package dev.unity.backend.gamebackend;

import dev.unity.backend.gamebackend.controllers.StripeController;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import dev.unity.backend.gamebackend.entity.User;
import dev.unity.backend.gamebackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StripeControllerTest {

    private UserRepository userRepository;
    private StripeController controller;
    private Authentication auth;
    private SecurityContext context;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        controller = new StripeController(userRepository);
        auth = mock(Authentication.class);
        context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("test@test.com");
    }

    @Test
    void createCheckoutSessionShouldReturnNotFoundIfUserMissing() throws Exception {
        when(userRepository.findByEmailIgnoreCase("test@test.com")).thenReturn(Optional.empty());

        ResponseEntity<Map<String,Object>> response = controller.createCheckoutSession(Map.of("priceId","price_123"));

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("USER_NOT_FOUND", response.getBody().get("error"));
    }

    @Test
    void createCheckoutSessionShouldReturnBadRequestIfPriceIdMissing() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmailIgnoreCase("test@test.com")).thenReturn(Optional.of(user));

        ResponseEntity<Map<String,Object>> response = controller.createCheckoutSession(Map.of());

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("MISSING_PRICE_ID", response.getBody().get("error"));
    }

    @Test
    void createCheckoutSessionShouldReturnUrlIfSuccess() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmailIgnoreCase("test@test.com")).thenReturn(Optional.of(user));

        // мок статичного Session.create
        try (MockedStatic<Session> mocked = mockStatic(Session.class)) {
            Session mockSession = new Session();
            mockSession.setId("sess_123");
            mockSession.setUrl("https://checkout.stripe.com/test");
            mocked.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(mockSession);

            ResponseEntity<Map<String,Object>> response = controller.createCheckoutSession(Map.of("priceId","price_123"));

            assertEquals(200, response.getStatusCodeValue());
            assertEquals("https://checkout.stripe.com/test", response.getBody().get("url"));
        }
    }
}
