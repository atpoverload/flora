package flora.strategy.contrib.ears;

import flora.Machine;
import flora.Meter;
import flora.examples.toggle.Toggle;
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

  private static class ToggleFactory implements RawWorkFactory<RangeConstrainedKnob, Toggle> {
    private final RangeConstrainedKnob[] knobs;

    private ToggleFactory(Toggle toggle) {
      this.knobs =
          Arrays.stream(toggle.knobs().asArray())
              .map(RangeConstrainedKnob::new)
              .toArray(RangeConstrainedKnob[]::new);
    }

    @Override
    public RangeConstrainedKnob[] knobs() {
      return this.knobs;
    }

    @Override
    public Toggle fromIndices(int[] configuration) {
      return Toggle.newFromArray(configuration);
    }
  }

  public static void main(String[] args) throws Exception {
    Machine machine =
        new Machine() {
          @Override
          public Map<String, Meter> meters() {
            return new HashMap<>(meters);
          }
        };

    CompatNumberProblem<RangeConstrainedKnob> problem =
        new CompatNumberProblem<>("toggle-problem", new ToggleFactory(Toggle.DEFAULT), machine);
    D_NSGAII nsga = new D_NSGAII();
    nsga.execute(new Task<>(problem, StopCriterion.EVALUATIONS, 100000, 0, 0));
  }
}
