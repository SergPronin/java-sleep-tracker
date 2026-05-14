package ru.yandex.practicum.sleeptracker.analysis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class NightMath {

  private NightMath() {
  }

  public static boolean overlapsMorningWindow(
      SleepingSession s, LocalDate night) {
    LocalDateTime wStart = night.atStartOfDay();
    LocalDateTime wEnd = night.atTime(6, 0);
    return s.getStartTime().isBefore(wEnd) && s.getEndTime().isAfter(wStart);
  }

  public static LocalDate firstNightToConsider(LocalDateTime firstSessionStart) {
    LocalTime noon = LocalTime.of(12, 0);
    LocalDate d = firstSessionStart.toLocalDate();
    if (firstSessionStart.toLocalTime().isAfter(noon)) {
      return d.plusDays(1);
    }
    return d;
  }

  public static LocalDate lastNightToConsider(LocalDateTime logEnd) {
    return logEnd.toLocalDate();
  }

  public static LocalDateTime loggingStart(List<SleepingSession> sessions) {
    return sessions.get(0).getStartTime();
  }

  public static LocalDateTime loggingEnd(List<SleepingSession> sessions) {
    return sessions.get(sessions.size() - 1).getEndTime();
  }

  public static boolean isSleeplessNight(
      LocalDate night, List<SleepingSession> sessions) {
    return sessions.stream().noneMatch(s -> overlapsMorningWindow(s, night));
  }

  public static boolean isDaytimeNap(SleepingSession s) {
    if (!s.getStartTime().toLocalDate().equals(s.getEndTime().toLocalDate())) {
      return false;
    }
    LocalTime a = s.getStartTime().toLocalTime();
    LocalTime b = s.getEndTime().toLocalTime();
    return a.isAfter(LocalTime.of(12, 0)) && b.isBefore(LocalTime.of(18, 0));
  }

  public static Stream<LocalDate> nightsBetween(LocalDate first, LocalDate last) {
    long count = ChronoUnit.DAYS.between(first, last) + 1;
    if (count <= 0) {
      return Stream.empty();
    }
    return LongStream.range(0, count).mapToObj(i -> first.plusDays(i));
  }
}