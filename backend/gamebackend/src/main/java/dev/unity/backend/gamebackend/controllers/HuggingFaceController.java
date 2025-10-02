package dev.unity.backend.gamebackend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/nlp")
public class HuggingFaceController {

    @Value("${hf.token}")
    private String hfToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/analyze")
    public ResponseEntity<String> analyze(@RequestBody Map<String, String> body) {
        String text = body.get("text");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(hfToken);

        Map<String, Object> payload = Map.of("inputs", text);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String url = "https://api-inference.huggingface.co/models/distilbert/distilbert-base-uncased-finetuned-sst-2-english";
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

      
        return ResponseEntity.ok(response.getBody());
    }
}
