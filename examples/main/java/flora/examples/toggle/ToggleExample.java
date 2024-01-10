package flora.examples.toggle;

import flora.Machine;
import flora.Meter;
import flora.meter.CpuJiffiesMeter;
import flora.meter.Stopwatch;
import flora.strategy.archiving.RandomArchivingStrategy;
import java.util.HashMap;
import java.util.Map;

/** A simple example for a {@link Machine} that adjusts toggles. */
class ToggleExample {
  private static final Map<String, Meter> meters =
      Map.of("stopwatch", new Stopwatch(), "jiffies", new CpuJiffiesMeter());

  public static void main(String[] args) {
    Machine machine =
        new Machine() {
          @Override
          public Map<String, Meter> meters() {
            return new HashMap<>(meters);
          }
        };
    RandomArchivingStrategy<?, ?, Toggle> strategy =
        new RandomArchivingStrategy<>(Toggle.DEFAULT);

    for (int i = 0; i < 100; i++) {
      Toggle workload = strategy.nextWorkload();
      Map<String, Double> measurement = machine.run(workload);
      strategy.update(workload, measurement);
    }

    System.out.println(strategy.records());
    System.out.println(strategy.summary());
  }
}
