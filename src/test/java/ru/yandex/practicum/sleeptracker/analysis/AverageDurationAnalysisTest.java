package ru.yandex.practicum.sleeptracker.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

class AverageDurationAnalysisTest {

  private final AverageDurationAnalysis analysis = new AverageDurationAnalysis();

  @Test
  void empty_returnsZero() {
    assertEquals(0L, (long) (Long) analysis.apply(List.of()).getValue());
  }

  @Test
  void sixtyAndOneTwenty_returnsNinetyRounded() {
    LocalDateTime t = LocalDateTime.of(2025, 10, 1, 22, 0);
    List<SleepingSession> sessions =
        List.of(
            new SleepingSession(t, t.plusMinutes(60), SleepQuality.GOOD),
            new SleepingSession(t.plusDays(1), t.plusDays(1).plusMinutes(120), SleepQuality.NORMAL));
    assertEquals(90L, (long) (Long) analysis.apply(sessions).getValue());
  }
}
