package ru.yandex.practicum.sleeptracker.analysis;

import java.util.List;
import java.util.function.Function;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

@FunctionalInterface
public interface SleepAnalysis extends Function<List<SleepingSession>, SleepAnalysisResult> {}