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
    } catch (Exception e) {
      // safely turn off the meters if we failed to run the workload
      meters.values().forEach(Meter::stop);
      meters.values().forEach(Meter::read);
      throw new IllegalArgumentException(
          "The workload failed with the given configuration, so the meters were stopped.", e);
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
