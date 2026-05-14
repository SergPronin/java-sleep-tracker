package ru.yandex.practicum.sleeptracker.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.model.Chronotype;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public class ChronotypeAnalysisTest {

  private final ChronotypeAnalysis analysis = new ChronotypeAnalysis();

  @Test
  public void empty_defaultsToPigeon() {
    assertEquals(Chronotype.PIGEON, analysis.apply(List.of()).getValue());
  }

  @Test
  public void tie_betweenOwlAndLark_returnsPigeon() {
    List<SleepingSession> sessions =
        List.of(
            new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 23, 30),
                LocalDateTime.of(2025, 10, 2, 10, 0),
                SleepQuality.GOOD),
            new SleepingSession(
                LocalDateTime.of(2025, 10, 2, 23, 30),
                LocalDateTime.of(2025, 10, 3, 10, 0),
                SleepQuality.GOOD),
            new SleepingSession(
                LocalDateTime.of(2025, 10, 3, 21, 0),
                LocalDateTime.of(2025, 10, 4, 6, 0),
                SleepQuality.GOOD),
            new SleepingSession(
                LocalDateTime.of(2025, 10, 4, 21, 0),
                LocalDateTime.of(2025, 10, 5, 6, 0),
                SleepQuality.GOOD));
    assertEquals(Chronotype.PIGEON, analysis.apply(sessions).getValue());
  }

  @Test
  public void majorityOwl_returnsOwl() {
    List<SleepingSession> sessions =
        List.of(
            new SleepingSession(
                LocalDateTime.of(2025, 11, 1, 23, 30),
                LocalDateTime.of(2025, 11, 2, 10, 0),
                SleepQuality.GOOD),
            new SleepingSession(
                LocalDateTime.of(2025, 11, 2, 23, 30),
                LocalDateTime.of(2025, 11, 3, 10, 0),
                SleepQuality.GOOD),
            new SleepingSession(
                LocalDateTime.of(2025, 11, 3, 23, 30),
                LocalDateTime.of(2025, 11, 4, 10, 0),
                SleepQuality.GOOD),
            new SleepingSession(
                LocalDateTime.of(2025, 11, 4, 21, 0),
                LocalDateTime.of(2025, 11, 5, 6, 0),
                SleepQuality.GOOD));
    assertEquals(Chronotype.OWL, analysis.apply(sessions).getValue());
  }

  @Test
  public void onlyDaytimeNap_noClassifiedNights_stillResolves() {
    List<SleepingSession> sessions =
        List.of(
            new SleepingSession(
                LocalDateTime.of(2025, 12, 1, 14, 30),
                LocalDateTime.of(2025, 12, 1, 15, 20),
                SleepQuality.NORMAL));
    assertEquals(Chronotype.PIGEON, analysis.apply(sessions).getValue());
  }
}
