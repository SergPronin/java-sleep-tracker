# Sleep Tracker Analyzer — полный гайд по ТЗ, подсказкам и коду

Документ объединяет **формулировки задания**, **подсказки курса**, **пошаговый план** и **пример полной реализации на Java** с пояснениями. Код внизу — **эталонный пример**: перенесите классы в проект, при необходимости подгоните граничные условия под автотесты Практикума.

---

## Содержание

1. [Постановка задачи и формат данных](#1-постановка-задачи-и-формат-данных)
2. [Жёсткое ограничение: никаких `for` и `while`](#2-жёсткое-ограничение-никаких-for-и-while)
3. [Сводная таблица требований ТЗ](#3-сводная-таблица-требований-тз)
4. [Подготовительные работы + подсказки курса](#4-подготовительные-работы--подсказки-курса)
5. [Реализация `SleepTrackerApp` и вывода](#5-реализация-sleeptrackerapp-и-вывода)
6. [Первая функция и базовые метрики](#6-первая-функция-и-базовые-метрики)
7. [Бессонные ночи](#7-бессонные-ночи)
8. [Хронотип](#8-хронотип)
9. [Финальный чеклист и сдача PR](#9-финальный-чеклист-и-сдача-pr)
10. [**Полная реализация: структура пакетов**](#10-полная-реализация-структура-пакетов)
11. [**Полный код по файлам с пояснениями**](#11-полный-код-по-файлам-с-пояснениями)
12. [**Примеры юнит-тестов**](#12-примеры-юнит-тестов)

---

## 1. Постановка задачи и формат данных

**Идея:** умные часы пишут лог сна. Приложение читает файл, запускает **набор аналитических функций** и выводит результаты так, чтобы пользователю было понятно, **что** посчитано.

**Формат строки:**

```text
дата и время засыпания;дата и время пробуждения;КАЧЕСТВО
```

**Качество:** `GOOD`, `NORMAL`, `BAD` → в коде `enum SleepQuality`.

**Парсинг:** `dd.MM.yy HH:mm`, разделитель полей `;`.

---

## 2. Жёсткое ограничение: никаких `for` и `while`

В **`src/main/java`** нельзя использовать **`for`** и **`while`**. В **тестах** можно.

**Замена:** `Stream`, `Optional`, `forEach` у коллекций, рекурсия.

---

## 3. Сводная таблица требований ТЗ

| Блок | Минимум тестов |
|------|----------------|
| Каркас + первая функция (число сессий) | ≥ **2** |
| 4 базовые метрики | ≥ **2** на **каждую** |
| Бессонные ночи | ≥ **4** |
| Хронотип | обязательно покрыть ключевые случаи |

---

## 4. Подготовительные работы + подсказки курса

### Модель

**Подсказка:** класс **`SleepingSession`**, качество — отдельный тип (**`enum`**).

### Хранение функций

**Подсказка 1:** каждая функция — **отдельный класс**, интерфейс **`Function`**, список в **`SleepTrackerApp`**, обход списка и вызов.

**На практике:** `SleepAnalysis extends Function<List<SleepingSession>, SleepAnalysisResult>`.

### Результат и вывод

**Подсказка 2:** класс-обёртка **`SleepAnalysisResult`** — **описание** + **значение**; печать из **`main`**.

---

## 5. Реализация `SleepTrackerApp` и вывода

1. `args[0]` — путь к файлу.
2. `SleepLogParser.parse(path)` → список сессий.
3. `List<SleepAnalysis> analyses = List.of(...)` — все анализаторы в нужном порядке.
4. Обход **без** `for`/`while`: `analyses.forEach(a -> { ... print ... })`.
5. Анализаторы **не** пишут в `System.out` — только возвращают `SleepAnalysisResult`.

---

## 6. Первая функция и базовые метрики

| Функция | Идея |
|---------|------|
| Число сессий | `sessions.size()` |
| Min / max / avg длительность (мин) | `mapToLong(getDurationMinutes).min/max/average` |
| Число BAD | `filter(quality == BAD).count()` |

Пустой список: для min/max/avg разумно **0** или `Optional` — **зафиксируйте в тестах** (в примере кода ниже — **0**).

---

## 7. Бессонные ночи

**Интервал логирования:** от **начала первой** сессии до **конца последней**.

**Бессонная ночь (дата `night`):** ни одна сессия **не пересекает** интервал **[`night` 0:00 ; `night` 6:00]**.

**Пересечение** отрезков `[start, end]` и окна `[wStart, wEnd]`:

```text
start < wEnd && end > wStart
```

**Правило первой сессии:** если первая сессия началась **после 12:00** — первая «потенциальная ночь» — **следующая** календарная ночь; если **до или в 12:00** — **предыдущая**. В примере кода это метод `firstNightToConsider`.

**Подсказка про `Period.between`:** границы периода при перечислении дат — уточните по документации; в примере используется **`Stream.iterate` + `limit`** по числу дней между первой и последней ночью включительно.

**Подсказка про месяцы:** используйте **`LocalDate` / `LocalDateTime`**, не номера дней вручную.

---

## 8. Хронотип

**Учитываем только «ночные» ситуации:** **бессонные ночи** и **дневные** сессии **не** участвуют.

**Дневная сессия (типичный признак из примеров ТЗ):** начало и конец в **один календарный день**, сон **днём** (в примере кода: строго **после 12:00** и **строго до 18:00** — как «тихий час» 14:30–15:20).

**Классификация одной ночи** по локальному времени засыпания и пробуждения выбранной сессии:

| Тип | Условие |
|-----|---------|
| Сова | засыпание **после** 23:00, пробуждение **после** 9:00 |
| Жаворонок | засыпание **до** 22:00, пробуждение **до** 7:00 |
| Голубь | всё остальное среди учитываемых |

**Итог:** максимум по счётчикам; при **любой ничьей** (двух или трёх лидеров) → **голубь**.

Результат функции — **`enum Chronotype`**, в `SleepAnalysisResult.getValue()`.

---

## 9. Финальный чеклист и сдача PR

- [ ] Весь вывод из **`main`**
- [ ] Все функции в списке
- [ ] Нет `for`/`while` в `src/main/java`
- [ ] Тесты по количеству из ТЗ
- [ ] Pull Request

---

## 10. Полная реализация: структура пакетов

Рекомендуемая структура (имена можно чуть менять):

```text
ru.yandex.practicum.sleeptracker
  SleepTrackerApp.java
ru.yandex.practicum.sleeptracker.model
  SleepingSession.java
  SleepQuality.java
  SleepAnalysisResult.java
  Chronotype.java
ru.yandex.practicum.sleeptracker.analysis
  SleepAnalysis.java
  CountSessionsAnalysis.java
  MinDurationAnalysis.java
  MaxDurationAnalysis.java
  AverageDurationAnalysis.java
  BadQualityCountAnalysis.java
  SleeplessNightsAnalysis.java
  ChronotypeAnalysis.java
  NightMath.java          // вспомогательные статические методы без циклов
ru.yandex.practicum.sleeptracker.parser
  SleepLogParser.java
```

---

## 11. Полный код по файлам с пояснениями

Ниже — **связный пример**. Смысл каждого блока дан в комментариях **перед** кодом.

### 11.1. `SleepQuality.java`

Перечисление из ТЗ; `valueOf` при парсинге строки файла.

```java
package ru.yandex.practicum.sleeptracker.model;

public enum SleepQuality {
  GOOD,
  NORMAL,
  BAD
}
```

---

### 11.2. `Chronotype.java`

Результат анализа хронотипа — не число, а тип (требование ТЗ).

```java
package ru.yandex.practicum.sleeptracker.model;

public enum Chronotype {
  OWL,
  LARK,
  PIGEON
}
```

---

### 11.3. `SleepAnalysisResult.java`

Подсказка курса: **описание** для человека + **значение** (число, `enum`, и т.д.).

```java
package ru.yandex.practicum.sleeptracker.model;

public class SleepAnalysisResult {

  private final String description;
  private final Object value;

  public SleepAnalysisResult(String description, Object value) {
    this.description = description;
    this.value = value;
  }

  public String getDescription() {
    return description;
  }

  public Object getValue() {
    return value;
  }
}
```

---

### 11.4. `SleepingSession.java`

Сессия: два момента времени + качество; длительность через `Duration`.

```java
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
```

---

### 11.5. `SleepAnalysis.java`

Функциональный интерфейс: список сессий → результат.

```java
package ru.yandex.practicum.sleeptracker.analysis;

import java.util.List;
import java.util.function.Function;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

@FunctionalInterface
public interface SleepAnalysis extends Function<List<SleepingSession>, SleepAnalysisResult> {}
```

---

### 11.6. Простые анализаторы (счётчик, min, max, avg, BAD)

Используют только `Stream`, без циклов. В Java — **один публичный класс на файл**; ниже каждый блок — отдельный файл.

**`CountSessionsAnalysis.java`**

```java
package ru.yandex.practicum.sleeptracker.analysis;

import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class CountSessionsAnalysis implements SleepAnalysis {

  @Override
  public SleepAnalysisResult apply(List<SleepingSession> sessions) {
    return new SleepAnalysisResult("Количество сессий сна", sessions.size());
  }
}
```

**`MinDurationAnalysis.java`**

```java
package ru.yandex.practicum.sleeptracker.analysis;

import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class MinDurationAnalysis implements SleepAnalysis {

  @Override
  public SleepAnalysisResult apply(List<SleepingSession> sessions) {
    long min =
        sessions.stream()
            .mapToLong(SleepingSession::getDurationMinutes)
            .min()
            .orElse(0L);
    return new SleepAnalysisResult("Минимальная продолжительность сессии (мин)", min);
  }
}
```

**`MaxDurationAnalysis.java`**

```java
package ru.yandex.practicum.sleeptracker.analysis;

import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class MaxDurationAnalysis implements SleepAnalysis {

  @Override
  public SleepAnalysisResult apply(List<SleepingSession> sessions) {
    long max =
        sessions.stream()
            .mapToLong(SleepingSession::getDurationMinutes)
            .max()
            .orElse(0L);
    return new SleepAnalysisResult("Максимальная продолжительность сессии (мин)", max);
  }
}
```

**`AverageDurationAnalysis.java`**

```java
package ru.yandex.practicum.sleeptracker.analysis;

import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class AverageDurationAnalysis implements SleepAnalysis {

  @Override
  public SleepAnalysisResult apply(List<SleepingSession> sessions) {
    double avg =
        sessions.stream().mapToLong(SleepingSession::getDurationMinutes).average().orElse(0.0);
    long rounded = Math.round(avg);
    return new SleepAnalysisResult("Средняя продолжительность сессии (мин)", rounded);
  }
}
```

**`BadQualityCountAnalysis.java`**

```java
package ru.yandex.practicum.sleeptracker.analysis;

import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class BadQualityCountAnalysis implements SleepAnalysis {

  @Override
  public SleepAnalysisResult apply(List<SleepingSession> sessions) {
    long bad =
        sessions.stream()
            .filter(s -> s.getSleepQuality() == SleepQuality.BAD)
            .count();
    return new SleepAnalysisResult("Количество сессий с качеством BAD", bad);
  }
}
```

---

### 11.7. `NightMath.java` — пересечения и «ночи» без `for`/`while`

Здесь собрана общая логика для бессонницы и хронотипа.

**Идеи:**

- Окно **0:00–6:00** для календарной даты `night`: `[night.atStartOfDay(), night.atTime(6,0)]`.
- Пересечение с сессией: `start.isBefore(windowEnd) && end.isAfter(windowStart)`.
- **Интервал логирования** пересекается с окном ночи — иначе эту дату не считаем (см. `nightWindowTouchesLogging`).
- **Первая ночь** по правилу 12:00 — `firstNightToConsider`.
- **Последняя ночь** — максимальная дата `d`, для которой окно ещё пересекается с логированием (ищем «сверху вниз» через `Stream`, без `while`).

```java
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

  private NightMath() {}

  /** Окно 0:00–6:00 для календарной даты night (утро этого дня). */
  public static boolean overlapsMorningWindow(
      SleepingSession s, LocalDate night) {
    LocalDateTime wStart = night.atStartOfDay();
    LocalDateTime wEnd = night.atTime(6, 0);
    return s.getStartTime().isBefore(wEnd) && s.getEndTime().isAfter(wStart);
  }

  /** Пересекается ли окно ночи с интервалом логирования [logStart; logEnd]. */
  public static boolean nightWindowTouchesLogging(
      LocalDate night, LocalDateTime logStart, LocalDateTime logEnd) {
    LocalDateTime wStart = night.atStartOfDay();
    LocalDateTime wEnd = night.atTime(6, 0);
    return logStart.isBefore(wEnd) && logEnd.isAfter(wStart);
  }

  /**
   * ТЗ: если первая сессия началась после 12:00 — «потенциальная ночь» со сдвигом на
   * следующую; иначе — на предыдущую.
   */
  public static LocalDate firstNightToConsider(LocalDateTime firstSessionStart) {
    LocalTime noon = LocalTime.of(12, 0);
    LocalDate d = firstSessionStart.toLocalDate();
    if (firstSessionStart.toLocalTime().isAfter(noon)) {
      return d.plusDays(1);
    }
    return d.minusDays(1);
  }

  public static LocalDateTime loggingStart(List<SleepingSession> sessions) {
    return sessions.get(0).getStartTime();
  }

  public static LocalDateTime loggingEnd(List<SleepingSession> sessions) {
    return sessions.get(sessions.size() - 1).getEndTime();
  }

  /**
   * Последняя календарная ночь (дата «утра»), для которой окно 0:00–6:00 ещё пересекается
   * с логированием. Перебор от logEnd.toLocalDate() вниз до firstNight.
   */
  public static LocalDate lastNightInclusive(
      LocalDate firstNight, LocalDateTime logStart, LocalDateTime logEnd) {
    LocalDate upper = logEnd.toLocalDate();
    long maxSteps = Math.max(0, ChronoUnit.DAYS.between(firstNight, upper) + 1);
    return Stream.iterate(upper, d -> d.minusDays(1))
        .limit(maxSteps)
        .filter(d -> !d.isBefore(firstNight))
        .filter(d -> nightWindowTouchesLogging(d, logStart, logEnd))
        .findFirst()
        .orElse(firstNight);
  }

  /** Бессонная ночь: ни одна сессия не пересекает окно 0:00–6:00. */
  public static boolean isSleeplessNight(
      LocalDate night, List<SleepingSession> sessions) {
    return sessions.stream().noneMatch(s -> overlapsMorningWindow(s, night));
  }

  /**
   * Дневной сон по образцу из ТЗ (14:30–15:20): один календарный день, строго после 12:00 и
   * строго до 18:00.
   */
  public static boolean isDaytimeNap(SleepingSession s) {
    if (!s.getStartTime().toLocalDate().equals(s.getEndTime().toLocalDate())) {
      return false;
    }
    LocalTime a = s.getStartTime().toLocalTime();
    LocalTime b = s.getEndTime().toLocalTime();
    return a.isAfter(LocalTime.of(12, 0)) && b.isBefore(LocalTime.of(18, 0));
  }

  /** Поток всех дат night от first до last включительно без for. */
  public static Stream<LocalDate> nightsBetween(LocalDate first, LocalDate last) {
    long count = ChronoUnit.DAYS.between(first, last) + 1;
    if (count <= 0) {
      return Stream.empty();
    }
    return LongStream.range(0, count).mapToObj(i -> first.plusDays(i));
  }
}
```

---

### 11.8. `SleeplessNightsAnalysis.java`

Подсчёт ночей в диапазоне `[firstNight; lastNight]`, для которых `isSleeplessNight`.

```java
package ru.yandex.practicum.sleeptracker.analysis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public final class SleeplessNightsAnalysis implements SleepAnalysis {

  @Override
  public SleepAnalysisResult apply(List<SleepingSession> sessions) {
    if (sessions.isEmpty()) {
      return new SleepAnalysisResult("Количество бессонных ночей", 0L);
    }
    LocalDateTime logStart = NightMath.loggingStart(sessions);
    LocalDateTime logEnd = NightMath.loggingEnd(sessions);
    LocalDate firstNight = NightMath.firstNightToConsider(logStart);
    LocalDate lastNight = NightMath.lastNightInclusive(firstNight, logStart, logEnd);

    long sleepless =
        NightMath.nightsBetween(firstNight, lastNight)
            .filter(n -> NightMath.nightWindowTouchesLogging(n, logStart, logEnd))
            .filter(n -> NightMath.isSleeplessNight(n, sessions))
            .count();

    return new SleepAnalysisResult("Количество бессонных ночей", sleepless);
  }
}
```

---

### 11.9. `ChronotypeAnalysis.java`

Для каждой ночи в диапазоне:

1. Окно этой ночи должно **пересекаться** с интервалом логирования.
2. Ночь **не** бессонная (есть пересечение с 0:00–6:00 хотя бы одной сессией).
3. Берём **представителя** сессии: не дневной сон, пересекает 0:00–6:00; если несколько — **самая длинная**.
4. По представителю: сначала проверяем **сову**, затем **жаворонка**, иначе — **голубь** (как в ТЗ: «остальные случаи»).

Дальше считаем `owl` / `lark` / `pigeon` и вызываем `resolve`: максимум частоты; если лидеров несколько — **голубь**.

```java
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
```

**Замечание:** приоритет «сова → жаворонок → голубь» внутри `classifyNight` нужен, если условия формально могут пересечься на искусственных данных; при корректных реальных сессиях курс обычно разводит случаи. Проверьте по **автотестам** порядок и границы времени.

---

### 11.10. `SleepLogParser.java`

Пустые строки отбрасываем; формат как в ТЗ.

```java
package ru.yandex.practicum.sleeptracker.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

public class SleepLogParser {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

  public List<SleepingSession> parse(String path) throws IOException {
    return Files.lines(Path.of(path))
        .filter(line -> !line.isBlank())
        .map(this::parseLine)
        .toList();
  }

  private SleepingSession parseLine(String line) {
    String[] parts = line.split(";");
    LocalDateTime start = LocalDateTime.parse(parts[0].trim(), FORMATTER);
    LocalDateTime end = LocalDateTime.parse(parts[1].trim(), FORMATTER);
    SleepQuality quality = SleepQuality.valueOf(parts[2].trim());
    return new SleepingSession(start, end, quality);
  }
}
```

---

### 11.11. `SleepTrackerApp.java`

Список всех функций и **весь вывод** здесь.

```java
package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.util.List;
import ru.yandex.practicum.sleeptracker.analysis.AverageDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.BadQualityCountAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.ChronotypeAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.CountSessionsAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.MaxDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.MinDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.SleepAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.SleeplessNightsAnalysis;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.parser.SleepLogParser;

public class SleepTrackerApp {

  private static final List<SleepAnalysis> ANALYSES =
      List.of(
          new CountSessionsAnalysis(),
          new MinDurationAnalysis(),
          new MaxDurationAnalysis(),
          new AverageDurationAnalysis(),
          new BadQualityCountAnalysis(),
          new SleeplessNightsAnalysis(),
          new ChronotypeAnalysis());

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Укажите путь к файлу лога сна");
      return;
    }
    String path = args[0];
    SleepLogParser parser = new SleepLogParser();
    try {
      List<SleepingSession> sessions = parser.parse(path);
      ANALYSES.forEach(
          analysis -> {
            SleepAnalysisResult result = analysis.apply(sessions);
            System.out.println(result.getDescription() + ": " + result.getValue());
          });
    } catch (IOException e) {
      System.err.println("Не удалось прочитать файл: " + e.getMessage());
    }
  }
}
```

---

## 12. Примеры юнит-тестов

В тестах **можно** использовать `for`/`while`. Ниже — идеи; адаптируйте под JUnit 5.

### 12.1. Счётчик сессий

```java
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analysis.CountSessionsAnalysis;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

class CountSessionsAnalysisTest {

  private final CountSessionsAnalysis analysis = new CountSessionsAnalysis();

  @Test
  void emptyList() {
    SleepAnalysisResult r = analysis.apply(List.of());
    assertEquals(0, r.getValue());
  }

  @Test
  void threeSessions() {
    LocalDateTime t = LocalDateTime.of(2025, 10, 1, 22, 0);
    List<SleepingSession> sessions =
        List.of(
            new SleepingSession(t, t.plusHours(8), SleepQuality.GOOD),
            new SleepingSession(t.plusDays(1), t.plusDays(1).plusHours(7), SleepQuality.NORMAL),
            new SleepingSession(t.plusDays(2), t.plusDays(2).plusHours(6), SleepQuality.BAD));
    assertEquals(3, analysis.apply(sessions).getValue());
  }
}
```

### 12.2. Бессонная ночь (один сценарий)

Идея: одна сессия 7:00–11:00 в день `D`; окно `D` 0:00–6:00 не пересекается → ночь бессонная (если попадает в диапазон учёта).

Проверяйте на малых списках с руками посчитанным ожиданием.

---

### 12.3. Хронотип — ничья

Две ночи «сова», две «жаворонок» → итог **голубь** (после исправления подсчёта через единый `classify` за ночь).

---

## Замечания к эталонному коду

1. **`ChronotypeAnalysis`:** `classifyNight` сначала проверяет сову, затем жаворонка, иначе голубь; дальше считаются частоты по списку `perNight`. Если тесты курса трактуют приоритет иначе — подстройте порядок проверок.
2. Границы **«после 23:00»** / **«до 22:00»** на границе минут уточните по тестам курса (`isAfter` / `isBefore`).
3. Разнесите **каждый публичный класс по своему `.java` файлу** — так требует Java.
4. После копирования кода прогоните **поиск** `for` и `while` в `src/main/java`.
5. **`NightMath.nightsBetween`:** при `first.isAfter(last)` вернётся пустой поток — для пустого списка сессий анализаторы выше уже обрабатываются отдельно; при слишком узком диапазоне ночей проверьте на бумаге.

---

*Отмечайте выполненные разделы галочками по мере работы.*
