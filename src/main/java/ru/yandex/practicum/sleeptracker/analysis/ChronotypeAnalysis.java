package ru.yandex.practicum.sleeptracker.analysis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import ru.yandex.practicum.sleeptracker.model.Chronotype;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class ChronotypeAnalysis implements SleepAnalysis {

  @Override
  public SleepAnalysisResult apply(List<SleepingSession> sessions) {
    if (sessions.isEmpty()) {
      return new SleepAnalysisResult("Хронотип пользователя", Chronotype.PIGEON);
    }
    LocalDateTime logStart = NightMath.loggingStart(sessions);
    LocalDateTime logEnd = NightMath.loggingEnd(sessions);
    LocalDate firstNight = NightMath.firstNightToConsider(logStart);
    LocalDate lastNight = NightMath.lastNightInclusive(firstNight, logStart, logEnd);

    List<Chronotype> perNight =
        NightMath.nightsBetween(firstNight, lastNight)
            .filter(n -> NightMath.nightWindowTouchesLogging(n, logStart, logEnd))
            .filter(n -> !NightMath.isSleeplessNight(n, sessions))
            .map(n -> pickRepresentativeSession(n, sessions))
            .flatMap(Optional::stream)
            .map(ChronotypeAnalysis::classifyNight)
            .toList();

    long owls = perNight.stream().filter(c -> c == Chronotype.OWL).count();
    long larks = perNight.stream().filter(c -> c == Chronotype.LARK).count();
    long pigeons = perNight.stream().filter(c -> c == Chronotype.PIGEON).count();

    Chronotype result = resolve(owls, larks, pigeons);
    return new SleepAnalysisResult("Хронотип пользователя", result);
  }

  /** Одна сессия на ночь: не дневной сон, пересекает окно 0:00–6:00; при нескольких — самая длинная. */
  private static Optional<SleepingSession> pickRepresentativeSession(
      LocalDate night, List<SleepingSession> sessions) {
    return sessions.stream()
        .filter(s -> !NightMath.isDaytimeNap(s))
        .filter(s -> NightMath.overlapsMorningWindow(s, night))
        .max(Comparator.comparingLong(SleepingSession::getDurationMinutes));
  }

  /** Сова имеет приоритет формулировки ТЗ над «остальными»; иначе жаворонок; иначе голубь. */
  private static Chronotype classifyNight(SleepingSession s) {
    if (isOwl(s)) {
      return Chronotype.OWL;
    }
    if (isLark(s)) {
      return Chronotype.LARK;
    }
    return Chronotype.PIGEON;
  }

  private static boolean isOwl(SleepingSession s) {
    LocalTime bed = s.getStartTime().toLocalTime();
    LocalTime wake = s.getEndTime().toLocalTime();
    return bed.isAfter(LocalTime.of(23, 0)) && wake.isAfter(LocalTime.of(9, 0));
  }

  private static boolean isLark(SleepingSession s) {
    LocalTime bed = s.getStartTime().toLocalTime();
    LocalTime wake = s.getEndTime().toLocalTime();
    return bed.isBefore(LocalTime.of(22, 0)) && wake.isBefore(LocalTime.of(7, 0));
  }

  private static Chronotype resolve(long owls, long larks, long pigeons) {
    long max = Math.max(Math.max(owls, larks), pigeons);
    int leaders = 0;
    if (owls == max) {
      leaders++;
    }
    if (larks == max) {
      leaders++;
    }
    if (pigeons == max) {
      leaders++;
    }
    if (leaders != 1) {
      return Chronotype.PIGEON;
    }
    if (owls == max) {
      return Chronotype.OWL;
    }
    if (larks == max) {
      return Chronotype.LARK;
    }
    return Chronotype.PIGEON;
  }
}