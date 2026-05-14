package ru.yandex.practicum.sleeptracker.analysis;

import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class MinDurationAnalysis implements SleepAnalysis {

  @Override
  public SleepAnalysisResult apply(List<SleepingSession> sessions) {
    long min =
        sessions.stream()
            .mapToLong(SleepingSession::getDurationMinutes)
            .min()
            .orElse(0L);
    return new SleepAnalysisResult("Минимальная продолжительность сессии (мин)", min);
  }
}