package dev.unity.backend.gamebackend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.unity.backend.gamebackend.controllers.HelloController;
import org.junit.jupiter.api.Test;

public class HelloControllerTest {
    @Test
void helloShouldReturnMessage() {
    HelloController controller = new HelloController();
    assertEquals("Привіт, бекенд працює!", controller.hello());
}

    
}
