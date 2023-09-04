package flora.testing;

import flora.Knob;
import flora.Machine;
import flora.Strategy;
import java.util.Map;

public final class SleepingMachine extends Machine {
  private final long sleepTimeMillis;

  public SleepingMachine(long sleepTimeMillis, Strategy strategy) {
    super(Map.of(), strategy);
    this.sleepTimeMillis = sleepTimeMillis;
  }

  @Override
  protected void runWorkload(Map<String, Knob> knobs) {
    try {
      Thread.sleep(sleepTimeMillis);
    } catch (Exception e) {

    }
  }
}
