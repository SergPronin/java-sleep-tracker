package ru.yandex.practicum.sleeptracker.analysis;

import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class AverageDurationAnalysis implements SleepAnalysis {

  @Override
  public SleepAnalysisResult apply(List<SleepingSession> sessions) {
    double avg =
        sessions.stream().mapToLong(SleepingSession::getDurationMinutes).average().orElse(0.0);
    long rounded = Math.round(avg);
    return new SleepAnalysisResult("Средняя продолжительность сессии (мин)", rounded);
  }
}