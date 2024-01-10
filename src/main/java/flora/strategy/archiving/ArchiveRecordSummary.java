package flora.strategy.archiving;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.summingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import flora.WorkUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A by-configuration summary of {@link ArchiveRecords}. Since this uses a {@link toMap}, users
 * should make sure their {@link WorkUnit} implements {@link equals} if they wish to use an {@code
 * ArchiveRecordSummary}.
 */
public class ArchiveRecordSummary<C> {
  private final Map<C, Long> counts;
  private final Map<C, Map<String, Double>> means;
  private final Map<C, Map<String, Double>> deviations;

  // TODO: got lazy and handwrote this. might be able to leverage DoubleSummaryStatistics
  <K, W extends WorkUnit<K, C>> ArchiveRecordSummary(List<ArchiveRecord<K, C, W>> records) {
    Map<C, List<ArchiveRecord<K, C, W>>> grouped =
        records.stream().collect(groupingBy(record -> record.workload().configuration(), toList()));
    counts =
        grouped.entrySet().stream()
            .collect(
                toMap(
                    e -> e.getKey(),
                    e ->
                        e.getValue().stream()
                            .flatMap(record -> record.measurement().entrySet().stream())
                            .collect(groupingBy(e1 -> e1.getKey(), counting()))
                            .values()
                            .stream()
                            .mapToLong(i -> (Long) i)
                            .max()
                            .getAsLong()));
    means =
        grouped.entrySet().stream()
            .collect(
                toMap(
                    e -> e.getKey(),
                    e ->
                        e.getValue().stream()
                            .flatMap(record -> record.measurement().entrySet().stream())
                            .collect(
                                groupingBy(
                                    e1 -> e1.getKey(), averagingDouble(e1 -> e1.getValue())))));
    deviations =
        grouped.entrySet().stream()
            .collect(
                toMap(
                    e -> e.getKey(),
                    e ->
                        e.getValue().stream()
                            .flatMap(record -> record.measurement().entrySet().stream())
                            .collect(
                                groupingBy(
                                    e1 -> e1.getKey(),
                                    summingDouble(
                                        e1 ->
                                            Math.pow(
                                                e1.getValue()
                                                    - means.get(e.getKey()).get(e1.getKey()),
                                                2))))
                            .entrySet()
                            .stream()
                            .collect(
                                toMap(
                                    e1 -> e1.getKey(),
                                    e1 -> Math.sqrt(e1.getValue() / counts.get(e.getKey()))))));
  }

  /** Returns the counts for each unique configuration. */
  public Map<C, Long> counts() {
    return new HashMap<>(counts);
  }

  /** Returns the mean for each unique configuration. */
  public Map<C, Map<String, Double>> means() {
    return new HashMap<>(means);
  }

  /** Returns the deviation for each unique configuration. */
  public Map<C, Map<String, Double>> deviations() {
    return new HashMap<>(deviations);
  }

  /** Returns all summary values as a json dict. */
  @Override
  public String toString() {
    return String.format(
        "{\"count\":{%s},\"mean\":{%s},\"std\":{%s}}",
        counts.entrySet().stream()
            .map(e -> String.format("\"%s\":%d", e.getKey(), e.getValue().longValue()))
            .collect(joining(",")),
        means.entrySet().stream()
            .map(
                e ->
                    String.format(
                        "\"%s\":{%s}",
                        e.getKey(),
                        e.getValue().entrySet().stream()
                            .map(e1 -> String.format("\"%s\":%.4f", e1.getKey(), e1.getValue()))
                            .collect(joining(","))))
            .collect(joining(",")),
        deviations.entrySet().stream()
            .map(
                e ->
                    String.format(
                        "\"%s\":{%s}",
                        e.getKey(),
                        e.getValue().entrySet().stream()
                            .map(e1 -> String.format("\"%s\":%.4f", e1.getKey(), e1.getValue()))
                            .collect(joining(","))))
            .collect(joining(",")));
  }
}
