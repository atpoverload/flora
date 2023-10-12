package flora.examples.toggle;

import flora.Machine;
import flora.Meter;
import flora.meter.CpuJiffiesMeter;
import flora.meter.Stopwatch;
import java.util.HashMap;
import java.util.Map;

/** A simple example for a {@link Machine} that adjusts toggles. */
public final class ToggleMachine extends Machine<ToggleKnobs, ToggleConfiguration, ToggleContext> {
  private final Map<String, Meter> meters =
      Map.of("stopwatch", new Stopwatch(), "jiffies", new CpuJiffiesMeter());

  public ToggleMachine() {}

  @Override
  public void runWorkload(ToggleContext context) {
    long sleepTime = (long) (10 * (1 + Math.random()));
    if (context.configuration().toggle1()) sleepTime += 3 * (1 + Math.random());
    if (context.configuration().toggle2()) sleepTime += 5 * (1 + Math.random());
    if (context.configuration().toggle1() && context.configuration().toggle2())
      sleepTime -= 12 * (1 + Math.random());
    try {
      Thread.sleep(Math.max(1, sleepTime));
    } catch (Exception e) {

    }
  }

  @Override
  public Map<String, Meter> meters() {
    return new HashMap<>(meters);
  }
}
