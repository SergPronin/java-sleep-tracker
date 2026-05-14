package ru.yandex.practicum.sleeptracker.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public class CountSessionsAnalysisTest {

  private final CountSessionsAnalysis analysis = new CountSessionsAnalysis();

  @Test
  public void emptyList_returnsZero() {
    assertEquals(0L, (long) (Long) analysis.apply(List.of()).getValue());
  }

  @Test
  public void threeSessions_returnsThree() {
    LocalDateTime t = LocalDateTime.of(2025, 10, 1, 22, 0);
    List<SleepingSession> sessions =
        List.of(
            new SleepingSession(t, t.plusHours(8), SleepQuality.GOOD),
            new SleepingSession(t.plusDays(1), t.plusDays(1).plusHours(7), SleepQuality.NORMAL),
            new SleepingSession(t.plusDays(2), t.plusDays(2).plusHours(6), SleepQuality.BAD));
    assertEquals(3L, (long) (Long) analysis.apply(sessions).getValue());
  }
}
