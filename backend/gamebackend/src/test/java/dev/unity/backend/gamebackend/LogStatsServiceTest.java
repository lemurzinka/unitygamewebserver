package dev.unity.backend.gamebackend;

import dev.unity.backend.gamebackend.services.LogStatsService;

import dev.unity.backend.gamebackend.repository.UserLoginRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogStatsServiceTest {

    private UserLoginRepository userLoginRepository;
    private LogStatsService logStatsService;

    @BeforeEach
    void setUp() {
        userLoginRepository = mock(UserLoginRepository.class);
        logStatsService = new LogStatsService(userLoginRepository);
    }

    @Test
    void getLoginStatsShouldReturnLabelsAndData() {
       
        List<Object[]> mockResults = List.of(
                new Object[]{"2026-05-13", 5L},
                new Object[]{"2026-05-14", 10L}
        );
        when(userLoginRepository.countLoginsPerDay()).thenReturn(mockResults);

        Map<String, Object> result = logStatsService.getLoginStats();

        assertEquals(List.of("2026-05-13", "2026-05-14"), result.get("labels"));
        assertEquals(List.of(5L, 10L), result.get("data"));
    }

    @Test
    void getLoginStatsShouldHandleEmptyResults() {
        when(userLoginRepository.countLoginsPerDay()).thenReturn(List.of());

        Map<String, Object> result = logStatsService.getLoginStats();

        assertEquals(List.of(), result.get("labels"));
        assertEquals(List.of(), result.get("data"));
    }
}
