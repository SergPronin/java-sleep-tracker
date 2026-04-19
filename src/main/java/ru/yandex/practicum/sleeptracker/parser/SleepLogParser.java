package ru.yandex.practicum.sleeptracker.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public class SleepLogParser {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public List<SleepingSession> parse(String path) throws IOException {
        return Files.lines(Path.of(path))
            .filter(line -> !line.isBlank())
            .map(this::parseLine)
            .toList();
    }

    private SleepingSession parseLine(String line) {
        String[] parts = line.split(";");
        LocalDateTime start = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(parts[1].trim(), FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2].trim());
        return new SleepingSession(start, end, quality);
    }
}