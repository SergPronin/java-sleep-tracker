package ru.yandex.practicum.sleeptracker.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.io.IOException;

public class SleepLogParser {

    public List<SleepingSession> parse(String path) throws IOException {
        return Files.lines(Path.of(path))
            .map(this::parseLine)
            .toList();
    }

    private SleepingSession parseLine(String line) {
        String[] parts = line.split(";");

        LocalDateTime start = parseDate(parts[0]);
        LocalDateTime end = parseDate(parts[1]);
        SleepQuality quality = SleepQuality.valueOf(parts[2]);

        return new SleepingSession(start, end, quality);
    }

    private LocalDateTime parseDate(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        return LocalDateTime.parse(str, formatter);
    }
}