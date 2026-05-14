package ru.yandex.practicum.sleeptracker.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public class BadQualityCountAnalysisTest {

  private final BadQualityCountAnalysis analysis = new BadQualityCountAnalysis();

  @Test
  public void empty_returnsZero() {
    assertEquals(0L, (long) (Long) analysis.apply(List.of()).getValue());
  }

  @Test
  public void twoBadOutOfThree() {
    LocalDateTime t = LocalDateTime.of(2025, 10, 1, 22, 0);
    List<SleepingSession> sessions =
        List.of(
            new SleepingSession(t, t.plusHours(8), SleepQuality.BAD),
            new SleepingSession(t.plusDays(1), t.plusDays(1).plusHours(7), SleepQuality.BAD),
            new SleepingSession(t.plusDays(2), t.plusDays(2).plusHours(6), SleepQuality.GOOD));
    assertEquals(2L, (long) (Long) analysis.apply(sessions).getValue());
  }
}
