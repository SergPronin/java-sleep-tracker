package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import ru.yandex.practicum.sleeptracker.analysis.AverageDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.BadQualityCountAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.ChronotypeAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.CountSessionsAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.MaxDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.MinDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.SleepAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.SleeplessNightsAnalysis;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.parser.SleepLogParser;

public class SleepTrackerApp {

    private final List<SleepAnalysis> analyses =
        List.of(
            new CountSessionsAnalysis(),
            new MinDurationAnalysis(),
            new MaxDurationAnalysis(),
            new AverageDurationAnalysis(),
            new BadQualityCountAnalysis(),
            new SleeplessNightsAnalysis(),
            new ChronotypeAnalysis());

    public static void main(String[] args) {
        new SleepTrackerApp().run(args);
    }

    private void run(String[] args) {
        if (args.length < 1) {
            System.err.println("Укажите путь к файлу лога сна");
            return;
        }
        String path = args[0];
        SleepLogParser parser = new SleepLogParser();
        try (Stream<String> lines = Files.lines(Path.of(path))) {
            List<SleepingSession> sessions = parser.parse(lines);
            analyses.forEach(
                analysis -> {
                    SleepAnalysisResult result = analysis.apply(sessions);
                    System.out.println(result.getDescription() + ": " + result.getValue());
                });
        } catch (IOException e) {
            System.err.println("Не удалось прочитать файл: " + e.getMessage());
        }
    }
}
