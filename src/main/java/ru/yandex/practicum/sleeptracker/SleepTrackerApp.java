package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.util.List;
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

    private static final List<SleepAnalysis> ANALYSES =
        List.of(
            new CountSessionsAnalysis(),
            new MinDurationAnalysis(),
            new MaxDurationAnalysis(),
            new AverageDurationAnalysis(),
            new BadQualityCountAnalysis(),
            new SleeplessNightsAnalysis(),
            new ChronotypeAnalysis());

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Укажите путь к файлу лога сна");
            return;
        }
        String path = args[0];
        SleepLogParser parser = new SleepLogParser();
        try {
            List<SleepingSession> sessions = parser.parse(path);
            ANALYSES.forEach(
                analysis -> {
                    SleepAnalysisResult result = analysis.apply(sessions);
                    System.out.println(result.getDescription() + ": " + result.getValue());
                });
        } catch (IOException e) {
            System.err.println("Не удалось прочитать файл: " + e.getMessage());
        }
    }
}