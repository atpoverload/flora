package flora.strategy.archiving;

import static java.util.stream.Collectors.joining;

import flora.WorkUnit;
import java.util.HashMap;
import java.util.Map;

/** A snapshot of a workload and measurement pair. */
public final class ArchiveRecord<K, C, W extends WorkUnit<K, C>> {
  private final Map<String, Double> measurement = new HashMap<>();

  private final W workload;

  ArchiveRecord(W workload, Map<String, Double> measurement) {
    this.workload = workload;
    measurement.forEach(this.measurement::put);
  }

  /** Returns the workload executed. */
  public W workload() {
    return workload;
  }

  /** Returns the measurement for the {@code workload's} execution. */
  public Map<String, Double> measurement() {
    return new HashMap<>(measurement);
  }

  /** Returns the record contents as a json dict. */
  @Override
  public String toString() {
    return String.format(
        "{\"workload\":%s,\"measurement\":{%s}}",
        workload,
        measurement.entrySet().stream()
            .map(e -> String.format("\"%s\":%f", e.getKey(), e.getValue()))
            .collect(joining(",")));
  }
}
