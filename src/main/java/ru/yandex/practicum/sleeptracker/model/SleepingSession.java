package ru.yandex.practicum.sleeptracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SleepingSession {

  private final LocalDateTime startTime;
  private final LocalDateTime endTime;
  private final SleepQuality sleepQuality;

  public SleepingSession(
      LocalDateTime startTime, LocalDateTime endTime, SleepQuality sleepQuality) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.sleepQuality = sleepQuality;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public SleepQuality getSleepQuality() {
    return sleepQuality;
  }

  public long getDurationMinutes() {
    return Duration.between(startTime, endTime).toMinutes();
  }
}