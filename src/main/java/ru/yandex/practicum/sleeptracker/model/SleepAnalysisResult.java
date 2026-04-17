package ru.yandex.practicum.sleeptracker.model;

public class SleepAnalysisResult {

  private String description;
  private Object value;

  public SleepAnalysisResult(String description, Object value) {
    this.description = description;
    this.value = value;
  }

  public String getDescription() { return description; }
  public Object getValue() { return value; }
}