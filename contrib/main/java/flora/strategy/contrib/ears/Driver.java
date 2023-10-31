package flora.strategy.contrib.ears;

import flora.Machine;
import flora.Meter;
import flora.WorkFactory;
import flora.examples.toggle.Toggle;
import flora.examples.toggle.ToggleConfiguration;
import flora.examples.toggle.ToggleFactory;
import flora.examples.toggle.ToggleKnobs;
import flora.meter.CpuJiffiesMeter;
import flora.meter.Stopwatch;
import java.util.HashMap;
import java.util.Map;
import org.um.feri.ears.algorithms.moo.nsga2.D_NSGAII;
import org.um.feri.ears.problems.StopCriterion;
import org.um.feri.ears.problems.Task;

final class Driver {
  private static final Map<String, Meter> meters =
      Map.of("stopwatch", new Stopwatch(), "jiffies", new CpuJiffiesMeter());
  public static void main(String[] args) throws Exception {
    Machine machine =
        new Machine() {
          @Override
          public Map<String, Meter> meters() {
            return new HashMap<>(meters);
          }
        };

    CompatNumberProblem problem =
        new CompatNumberProblem("toggle-problem", new ToggleFactory(), machine);
    D_NSGAII nsga = new D_NSGAII();
    nsga.execute(new Task<>(problem, StopCriterion.EVALUATIONS, 100, 0, 0));
  }
}
