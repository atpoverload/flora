package flora;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;

/** An interface for a measurable work unit. */
public final class MeteringMachine {
  private final HashMap<String, Meter> meters = new HashMap<>();

  public MeteringMachine(Map<String, Meter> meters) {
    meters.forEach(this.meters::put);
  }

  /** Meters some unit of work. */
  public final <K, C, W extends WorkUnit<K, C>> Map<String, Double> run(W workload) {
    meters.values().forEach(Meter::start);
    try {
      workload.run();
    } catch (Exception error) {
      // safely turn off the meters if we failed to run the workload
      meters.values().forEach(Meter::stop);
      // TODO: we may want a "partial" measurement here?
      meters.values().forEach(Meter::read);
      if (error instanceof PerformanceFault) {
        throw error;
      } else {
        throw new IllegalArgumentException(
            String.format("Failed to run %s, so the meters were stopped.", workload), error);
      }
    }
    meters.values().forEach(Meter::stop);
    Map<String, Double> measurement =
        meters.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().read()));
    return measurement;
  }

  /** Meters used to measure the work units. */
  public final String[] meters() {
    return meters.keySet().toArray(String[]::new);
  }
}
