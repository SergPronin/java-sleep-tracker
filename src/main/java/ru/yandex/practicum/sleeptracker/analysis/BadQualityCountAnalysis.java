package ru.yandex.practicum.sleeptracker.analysis;

import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class BadQualityCountAnalysis implements SleepAnalysis {

  @Override
  public SleepAnalysisResult apply(List<SleepingSession> sessions) {
    long bad =
        sessions.stream()
            .filter(s -> s.getSleepQuality() == SleepQuality.BAD)
            .count();
    return new SleepAnalysisResult("Количество сессий с качеством BAD", bad);
  }
}