package dev.unity.backend.gamebackend.controllers;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import dev.unity.backend.gamebackend.entity.User;
import dev.unity.backend.gamebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.Map;


@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeController {

    private static final Logger logger = LoggerFactory.getLogger(StripeController.class);

    private final UserRepository userRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/create-checkout-session")
public ResponseEntity<Map<String, Object>> createCheckoutSession(@RequestBody Map<String, Object> data) throws StripeException {
    Stripe.apiKey = stripeApiKey;

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName(); 

    logger.info("🔍 Looking for user with email: {}", email);

    User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
    if (user == null) {
        logger.warn("❌ No user found with email: {}", email);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "USER_NOT_FOUND", "message", "User not found"));
    }

    String priceId = (String) data.get("priceId");
    if (priceId == null || priceId.isBlank()) {
        logger.warn("❌ Missing priceId in request body");
        return ResponseEntity.badRequest().body(Map.of("error", "MISSING_PRICE_ID"));
    }

    SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("http://localhost:3001/success")
            .setCancelUrl("http://localhost:3001/cancel")
            .setClientReferenceId(user.getId().toString())
            .addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPrice(priceId)
                            .build()
            )
            .build();

    Session session = Session.create(params);

    logger.info("Created checkout session {} for user {}", session.getId(), user.getId());

    return ResponseEntity.ok(Map.of("url", session.getUrl()));
}



@PostMapping("/webhook")
public ResponseEntity<Map<String, Object>> handleStripeEvent(
        @RequestBody String payload,
        @RequestHeader("Stripe-Signature") String sigHeader
) {
    logger.info("🚀 Webhook endpoint invoked!");

    try {
        Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        logger.info("=== Stripe Webhook ===");
        logger.info("Event type: {}", event.getType());

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            String sessionId = (session != null) ? session.getId()
                    : new com.fasterxml.jackson.databind.ObjectMapper()
                        .readTree(event.getData().getObject().toJson())
                        .get("id").asText();

            Session fullSession = Session.retrieve(
                    sessionId,
                    SessionRetrieveParams.builder().build(),
                    RequestOptions.builder().setApiKey(stripeApiKey).build()
            );

            Long userId = Long.valueOf(fullSession.getClientReferenceId());
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.ok(Map.of("error", "USER_NOT_FOUND"));
            }

          
            String paymentIntentId = fullSession.getPaymentIntent();
            com.stripe.model.PaymentIntent intent = com.stripe.model.PaymentIntent.retrieve(
                    paymentIntentId,
                    RequestOptions.builder().setApiKey(stripeApiKey).build()
            );

            long amount = intent.getAmountReceived(); 
            logger.info("✅ PaymentIntent received, amount={} cents", amount);

           
            int coins;
            if (amount == 500) coins = 50;     
            else if (amount == 1000) coins = 100; 
            else if (amount == 2000) coins = 200; 
            else coins = 0;

            user.setBalance(user.getBalance() + coins);
            userRepository.save(user);

            logger.info("💾 User {} (id={}) saved. Added {} coins. New balance={}",
                    user.getUsername(), user.getId(), coins, user.getBalance());

            return ResponseEntity.ok(Map.of(
                "message", "Balance updated",
                "userId", user.getId(),
                "username", user.getUsername(),
                "balance", user.getBalance()
            ));
        }

        return ResponseEntity.ok(Map.of("message", "Event ignored"));
    } catch (SignatureVerificationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "INVALID_SIGNATURE"));
    } catch (Exception e) {
        logger.error("❌ Error processing webhook", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "WEBHOOK_ERROR"));
    }
}




    @PostMapping("/test")
public ResponseEntity<?> test() {
    return ResponseEntity.ok(Map.of("message", "Stripe controller is alive"));
}

}
