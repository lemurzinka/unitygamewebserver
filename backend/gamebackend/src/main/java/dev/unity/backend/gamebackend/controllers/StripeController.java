package dev.unity.backend.gamebackend.controllers;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, Object>> createCheckoutSession(@RequestBody Map<String, Object> data) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        String priceId = (String) data.get("priceId");

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3001/success")
                .setCancelUrl("http://localhost:3001/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPrice(priceId)
                                .build()
                )
                .build();

        Session session = Session.create(params);

        return ResponseEntity.ok(Map.of("url", session.getUrl()));
    }

    @PostMapping("/webhook")
public ResponseEntity<String> handleStripeEvent(
    @RequestBody String payload,
    @RequestHeader("Stripe-Signature") String sigHeader
) {
    try {
        Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

        
        System.out.println("Event received: " + event.getType());

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                System.out.println("Payment successful for session: " + session.getId());
            }
        }

        return ResponseEntity.ok("Received");
    } catch (SignatureVerificationException e) {
        System.out.println("Invalid signature: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
    }
}
}
