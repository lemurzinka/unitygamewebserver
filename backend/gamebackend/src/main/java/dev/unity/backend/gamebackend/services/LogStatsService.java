package dev.unity.backend.gamebackend.services;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.stream.Collectors;

@Service
public class LogStatsService {

    private static final String LOG_DIR = "logs";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Map<String, Object> getLoginStats() throws IOException {
        List<String> allLines = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(LOG_DIR))) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    if (path.toString().endsWith(".gz")) {
                     
                        try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(path.toFile()));
                             BufferedReader br = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8))) {
                            allLines.addAll(br.lines().toList());
                        }
                    } else {
                     
                        allLines.addAll(Files.readAllLines(path));
                    }
                }
            }
        }

   
        Map<LocalDate, Long> counts = allLines.stream()
                .filter(line -> line.contains("User logged in successfully"))
                .collect(Collectors.groupingBy(
                        line -> LocalDate.parse(line.substring(0, 10), FORMATTER),
                        Collectors.counting()
                ));

        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        counts.keySet().stream().sorted().forEach(date -> {
            labels.add(date.toString());
            data.add(counts.get(date));
        });

        return Map.of("labels", labels, "data", data);
    }
}
