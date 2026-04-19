package ru.yandex.practicum.sleeptracker.analysis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class SleeplessNightsAnalysis implements SleepAnalysis {

  @Override
  public SleepAnalysisResult apply(List<SleepingSession> sessions) {
    if (sessions.isEmpty()) {
      return new SleepAnalysisResult("Количество бессонных ночей", 0L);
    }
    LocalDateTime logStart = NightMath.loggingStart(sessions);
    LocalDateTime logEnd = NightMath.loggingEnd(sessions);
    LocalDate firstNight = NightMath.firstNightToConsider(logStart);
    LocalDate lastNight = NightMath.lastNightInclusive(firstNight, logStart, logEnd);

    long sleepless =
        NightMath.nightsBetween(firstNight, lastNight)
            .filter(n -> NightMath.nightWindowTouchesLogging(n, logStart, logEnd))
            .filter(n -> NightMath.isSleeplessNight(n, sessions))
            .count();

    return new SleepAnalysisResult("Количество бессонных ночей", sleepless);
  }
}