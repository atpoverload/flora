package flora.strategy.contrib.ears;

import flora.Machine;
import flora.Meter;
import flora.examples.toggle.Toggle;
import flora.examples.toggle.ToggleKnobs;
import flora.knob.meta.RangeConstrainedKnob;
import flora.meter.CpuJiffiesMeter;
import flora.meter.Stopwatch;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.um.feri.ears.algorithms.moo.nsga2.D_NSGAII;
import org.um.feri.ears.problems.StopCriterion;
import org.um.feri.ears.problems.Task;

final class Driver {
  private static final Map<String, Meter> meters =
      Map.of("stopwatch", new Stopwatch(), "jiffies", new CpuJiffiesMeter());

  private static final RangeConstrainedKnob[] knobs =
      Arrays.stream(ToggleKnobs.INSTANCE.asArray())
          .map(RangeConstrainedKnob::new)
          .toArray(RangeConstrainedKnob[]::new);

  public static void main(String[] args) throws Exception {
    Machine machine =
        new Machine() {
          @Override
          public Map<String, Meter> meters() {
            return new HashMap<>(meters);
          }
        };

    CompatNumberProblem<RangeConstrainedKnob> problem =
        new CompatNumberProblem<>("toggle-problem", knobs, Toggle.DEFAULT, machine);
    D_NSGAII nsga = new D_NSGAII();
    nsga.execute(new Task<>(problem, StopCriterion.EVALUATIONS, 100000, 0, 0));
  }
}
