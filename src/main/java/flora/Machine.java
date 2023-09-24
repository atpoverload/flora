package flora;

import static java.util.stream.Collectors.toMap;

import java.util.Map;

/** An interface for a measurable work unit. */
public abstract class Machine<K, C, Ctx extends WorkloadContext<K, C>> {
  public final Map<String, Double> run(Ctx context) {
    Map<String, Meter> meters = meters();
    meters.values().forEach(Meter::start);
    try {
      runWorkload(context);
    } catch (Exception e) {
      // safely turn off the meters if we failed to run the workload
      meters.values().forEach(Meter::stop);
      meters.values().forEach(Meter::read);
      throw new IllegalArgumentException(
          "The workload failed with the given configuration, so the meters were stopped.", e);
    }
    meters.values().forEach(Meter::stop);
    return meters.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().read()));
  }

  /** Unit of work. */
  protected abstract void runWorkload(Ctx context);

  /** Returns the meters used by the machine. */
  public abstract Map<String, Meter> meters();
}
