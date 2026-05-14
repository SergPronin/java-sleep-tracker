package ru.yandex.practicum.sleeptracker.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public class SleeplessNightsAnalysisTest {

  private final SleeplessNightsAnalysis analysis = new SleeplessNightsAnalysis();

  @Test
  public void empty_returnsZero() {
    assertEquals(0L, (long) (Long) analysis.apply(List.of()).getValue());
  }

  @Test
  public void sleepFrom23to03_notSleepless() {
    LocalDateTime start = LocalDateTime.of(2025, 10, 1, 23, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 2, 3, 0);
    List<SleepingSession> sessions = List.of(new SleepingSession(start, end, SleepQuality.GOOD));
    assertEquals(0L, (long) (Long) analysis.apply(sessions).getValue());
  }

  @Test
  public void sleepFrom2to7_notSleepless() {
    LocalDateTime start = LocalDateTime.of(2025, 10, 2, 2, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 2, 7, 0);
    List<SleepingSession> sessions = List.of(new SleepingSession(start, end, SleepQuality.GOOD));
    assertEquals(0L, (long) (Long) analysis.apply(sessions).getValue());
  }

  @Test
  public void sleepOnly7to11_isSleeplessNight() {
    LocalDateTime start = LocalDateTime.of(2025, 10, 3, 7, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 3, 11, 0);
    List<SleepingSession> sessions = List.of(new SleepingSession(start, end, SleepQuality.NORMAL));
    assertEquals(1L, (long) (Long) analysis.apply(sessions).getValue());
  }

  @Test
  public void firstSessionAfterNoon_skipsMorningWithoutOverlap() {
    LocalDateTime napStart = LocalDateTime.of(2025, 10, 3, 14, 0);
    LocalDateTime napEnd = LocalDateTime.of(2025, 10, 3, 15, 0);
    LocalDateTime nightStart = LocalDateTime.of(2025, 10, 4, 23, 0);
    LocalDateTime nightEnd = LocalDateTime.of(2025, 10, 5, 8, 0);
    List<SleepingSession> sessions =
        List.of(
            new SleepingSession(napStart, napEnd, SleepQuality.NORMAL),
            new SleepingSession(nightStart, nightEnd, SleepQuality.GOOD));
    assertEquals(1L, (long) (Long) analysis.apply(sessions).getValue());
  }
}
