package flora.strategy.archiving;

import static java.util.stream.Collectors.joining;

import flora.WorkloadContext;
import java.util.HashMap;
import java.util.Map;

/** A snapshot of a context and measurement pair. */
public final class ArchiveRecord<K, C, Ctx extends WorkloadContext<K, C>> {
  private final Map<String, Double> measurement = new HashMap<>();

  private final Ctx context;

  ArchiveRecord(Ctx context, Map<String, Double> measurement) {
    this.context = context;
    measurement.forEach(this.measurement::put);
  }

  public Ctx context() {
    return context;
  }

  public Map<String, Double> measurement() {
    return new HashMap<>(measurement);
  }

  /** Returns the record contents as a json dict. */
  @Override
  public String toString() {
    return String.format(
        "{\"context\":%s,\"measurement\":{%s}}",
        context,
        measurement.entrySet().stream()
            .map(e -> String.format("\"%s\":%f", e.getKey(), e.getValue()))
            .collect(joining(",")));
  }
}
