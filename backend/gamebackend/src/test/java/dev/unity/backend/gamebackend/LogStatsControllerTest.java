package dev.unity.backend.gamebackend;

import dev.unity.backend.gamebackend.controllers.LogStatsController;

import dev.unity.backend.gamebackend.services.LogStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogStatsControllerTest {

    private LogStatsService logStatsService;
    private LogStatsController controller;

    @BeforeEach
    void setUp() {
        logStatsService = mock(LogStatsService.class);
        controller = new LogStatsController(logStatsService);
    }

    @Test
    void getLoginStatsShouldReturnServiceResult() throws IOException {
        Map<String, Object> mockStats = Map.of("totalLogins", 42);
        when(logStatsService.getLoginStats()).thenReturn(mockStats);

        Map<String, Object> result = controller.getLoginStats();

        assertEquals(42, result.get("totalLogins"));
        verify(logStatsService).getLoginStats();
    }
}
