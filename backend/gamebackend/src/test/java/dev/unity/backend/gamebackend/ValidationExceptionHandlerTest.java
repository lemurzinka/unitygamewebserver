package dev.unity.backend.gamebackend;

import dev.unity.backend.gamebackend.controllers.ValidationExceptionHandler;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidationExceptionHandlerTest {

    @Test
    void handleValidationErrorsShouldReturnFieldErrors() {
        
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("objectName", "username", "Username is required"),
                new FieldError("objectName", "email", "Email is invalid")
        ));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ValidationExceptionHandler handler = new ValidationExceptionHandler();
        ResponseEntity<?> response = handler.handleValidationErrors(ex);

        assertEquals(400, response.getStatusCodeValue());
        Map<?,?> body = (Map<?,?>) response.getBody();
        assertEquals("Username is required", body.get("username"));
        assertEquals("Email is invalid", body.get("email"));
    }
}
