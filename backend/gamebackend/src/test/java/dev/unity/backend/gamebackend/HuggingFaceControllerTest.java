package dev.unity.backend.gamebackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.unity.backend.gamebackend.entity.Feedback;
import dev.unity.backend.gamebackend.repository.FeedbackRepository;
import dev.unity.backend.gamebackend.controllers.HuggingFaceController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HuggingFaceControllerTest {

    private FeedbackRepository feedbackRepository;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private HuggingFaceController controller;

    @BeforeEach
    void setUp() {
        feedbackRepository = mock(FeedbackRepository.class);
        restTemplate = mock(RestTemplate.class);
        objectMapper = new ObjectMapper();

        controller = new HuggingFaceController(feedbackRepository);

       
        ReflectionTestUtils.setField(controller, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(controller, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(controller, "hfToken", "dummy-token");
    }

    @Test
    void analyzeShouldReturnParsedLabelAndScore() throws Exception {
      
        String mockResponse = "[[ {\"label\":\"POSITIVE\",\"score\":0.95} ]]";
        ResponseEntity<String> mockEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockEntity);

        Map<String, String> body = Map.of("text", "I love this!");

        ResponseEntity<?> response = controller.analyze(body);

        assertEquals(200, response.getStatusCodeValue());
        Map<?,?> result = (Map<?,?>) response.getBody();
        assertEquals("POSITIVE", result.get("label"));
        assertEquals(0.95, result.get("score"));
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void analyzeShouldReturnErrorOnEmptyResponse() throws Exception {
    
        String mockResponse = "[[]]";
        ResponseEntity<String> mockEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockEntity);

        Map<String, String> body = Map.of("text", "test");

        ResponseEntity<?> response = controller.analyze(body);

        assertEquals(502, response.getStatusCodeValue());
        Map<?,?> result = (Map<?,?>) response.getBody();
        assertEquals("EMPTY_RESPONSE", result.get("error"));
    }
}
