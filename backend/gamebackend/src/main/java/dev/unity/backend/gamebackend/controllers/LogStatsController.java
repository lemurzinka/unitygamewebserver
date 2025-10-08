package dev.unity.backend.gamebackend.controllers;

import dev.unity.backend.gamebackend.services.LogStatsService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogStatsController {

    private final LogStatsService logStatsService;

    public LogStatsController(LogStatsService logStatsService) {
        this.logStatsService = logStatsService;
    }

    @GetMapping("/logins")
    public Map<String, Object> getLoginStats() throws IOException {
        return logStatsService.getLoginStats();
    }
}