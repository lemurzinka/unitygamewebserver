package dev.unity.backend.gamebackend.services;

import dev.unity.backend.gamebackend.repository.UserLoginRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LogStatsService {

    private final UserLoginRepository userLoginRepository;

    public LogStatsService(UserLoginRepository userLoginRepository) {
        this.userLoginRepository = userLoginRepository;
    }

    public Map<String, Object> getLoginStats() {
        List<Object[]> results = userLoginRepository.countLoginsPerDay();

        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (Object[] row : results) {
            labels.add(row[0].toString()); // date
            data.add((Long) row[1]);       // count
        }

        return Map.of("labels", labels, "data", data);
    }
}
