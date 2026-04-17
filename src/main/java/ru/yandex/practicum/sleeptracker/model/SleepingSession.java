package ru.yandex.practicum.sleeptracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SleepingSession {
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private SleepQuality sleepQuality;

  public SleepingSession(LocalDateTime startTime, LocalDateTime endTime, SleepQuality sleepQuality) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.sleepQuality = sleepQuality;
  }

  public long getDurationMinutes() {
    return Duration.between(startTime, endTime).toMinutes();
  }
}
