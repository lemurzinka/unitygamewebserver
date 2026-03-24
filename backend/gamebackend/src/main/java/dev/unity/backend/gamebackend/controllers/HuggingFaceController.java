package dev.unity.backend.gamebackend.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.unity.backend.gamebackend.entity.Feedback;
import dev.unity.backend.gamebackend.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nlp")
public class HuggingFaceController {

    private static final Logger logger = LoggerFactory.getLogger(HuggingFaceController.class);

    @Value("${hf.token}")
    private String hfToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FeedbackRepository feedbackRepository;

    public HuggingFaceController(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        logger.info("User submitted text for sentiment analysis: {}", text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(hfToken);

        Map<String, Object> payload = Map.of("inputs", text);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        String url = "https://router.huggingface.co/hf-inference/models/siebert/sentiment-roberta-large-english";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            logger.info("Raw Hugging Face response: {}", response.getBody());

            List<List<Map<String, Object>>> outer = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<>() {}
            );

            if (outer.isEmpty() || outer.get(0).isEmpty()) {
                logger.warn("⚠️ Empty response");
                return ResponseEntity.status(502).body(Map.of("error", "EMPTY_RESPONSE"));
            }

            Map<String, Object> best = outer.get(0).get(0);
            String label = ((String) best.get("label")).toUpperCase();
            Double score = (Double) best.get("score");

            logger.info("✅ Parsed label: {}, score={}", label, score);

          
            Feedback feedback = Feedback.builder()
                    .message(text)
                    .sentiment(label)
                    .score(score)
                    .build();

            feedbackRepository.save(feedback);

            return ResponseEntity.ok(Map.of("label", label, "score", score));
        } catch (Exception e) {
            logger.error("❌ Error calling Hugging Face API", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "HUGGINGFACE_API_ERROR",
                    "message", e.getMessage()
            ));
        }
    }
}
