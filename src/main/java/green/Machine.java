package green;

import static java.util.stream.Collectors.toMap;

import java.util.Map;

/** A class for a metered and evaluated work unit. */
public abstract class Machine {
  private final Map<String, Meter> meters;
  private final Strategy strategy;

  protected Machine(Map<String, Meter> meters, Strategy strategy) {
    this.meters = meters;
    this.strategy = strategy;
  }

  /**
   * Executes the underlying workload in {@code runWorkload} with meters running. Once the work unit
   * is done, the used knob configuration and measurement is evaluated by the {@link Strategy}.
   */
  public final void run() {
    Map<String, Knob> knobs = strategy.nextConfiguration();
    meters.values().forEach(Meter::start);
    try {
      runWorkload(knobs);
    } catch (Exception e) {
      // safely turn off the meters if we failed to run the workload
      meters.values().forEach(Meter::stop);
      meters.values().forEach(Meter::read);
      throw new IllegalArgumentException(
          "The workload failed with the given configuration, so the meters were stopped.", e);
    }
    meters.values().forEach(Meter::stop);
    strategy.update(
        knobs,
        meters.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().read())));
  }

  /** Unit of work. */
  protected abstract void runWorkload(Map<String, Knob> knobs);
}
