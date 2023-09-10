package flora.machine;

import static java.util.stream.Collectors.toMap;

import flora.KnobContext;
import flora.Machine;
import flora.Meter;
import java.util.Map;

public final class MachineRunner {
  /**
   * Executes {@link runWorkload} with the {@link strategy} context and {@link meters} running. The
   * context and measurement are fed back to the {@link strategy}.
   */
  public static final <K, C, KC extends KnobContext<K, C>> void run(Machine<K, C, KC> machine) {
    KC context = machine.strategy().context();
    Map<String, Meter> meters = machine.meters();
    meters.values().forEach(Meter::start);
    try {
      machine.runWorkload(context);
    } catch (Exception e) {
      // safely turn off the meters if we failed to run the workload
      meters.values().forEach(Meter::stop);
      meters.values().forEach(Meter::read);
      throw new IllegalArgumentException(
          "The workload failed with the given configuration, so the meters were stopped.", e);
    }
    meters.values().forEach(Meter::stop);
    machine
        .strategy()
        .update(
            context,
            meters.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().read())));
  }
}
