package ru.yandex.practicum.sleeptracker.analysis;

import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class MaxDurationAnalysis implements SleepAnalysis {

  @Override
  public SleepAnalysisResult apply(List<SleepingSession> sessions) {
    long max =
        sessions.stream()
            .mapToLong(SleepingSession::getDurationMinutes)
            .max()
            .orElse(0L);
    return new SleepAnalysisResult("Максимальная продолжительность сессии (мин)", max);
  }
}