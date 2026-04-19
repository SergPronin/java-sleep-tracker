package ru.yandex.practicum.sleeptracker.analysis;

import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class CountSessionsAnalysis implements SleepAnalysis {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult("Количество сессий сна", sessions.size());
    }
}