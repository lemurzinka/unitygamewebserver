package dev.unity.backend.gamebackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
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

    @PostMapping("/analyze")
public ResponseEntity<?> analyze(@RequestBody Map<String, String> body) {
    String text = body.get("text");
    logger.info("User submitted text for sentiment analysis: {}", text);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.setBearerAuth(hfToken);

    Map<String, Object> payload = Map.of(
        "inputs", text,
        "model", "siebert/sentiment-roberta-large-english"
    );

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
    String url = "https://router.huggingface.co";

    try {
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        logger.info("Raw Hugging Face response: {}", response.getBody());

        Map<String,Object> root = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        Object outputsObj = root.get("outputs");

        if (!(outputsObj instanceof List)) {
            logger.warn("⚠️ Unexpected response format");
            return ResponseEntity.status(502).body(Map.of("error", "INVALID_RESPONSE"));
        }

        List<Map<String,Object>> outputs = (List<Map<String,Object>>) outputsObj;
        if (outputs.isEmpty() || !outputs.get(0).containsKey("label")) {
            logger.warn("⚠️ No label in response");
            return ResponseEntity.status(502).body(Map.of("error", "NO_LABEL"));
        }

        String label = ((String) outputs.get(0).get("label")).toUpperCase();
        logger.info("✅ Parsed label: {}", label);

        return ResponseEntity.ok(Map.of("label", label));
    } catch (Exception e) {
        logger.error("❌ Error calling Hugging Face API", e);
        return ResponseEntity.status(500).body(Map.of("error", "HUGGINGFACE_API_ERROR", "message", e.getMessage()));
    }
}

}
