package flora.strategy.contrib.ears;

import static java.util.stream.Collectors.toList;

import flora.Knob;
import flora.Strategy;
import flora.WorkloadContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class CompatNumberProblem<Ctx extends WorkloadContext<?, ?>>
    implements Strategy<EarsKnob[], int[], EarsContext> {
  public final ArrayList<Map<String, Double>> measurements = new ArrayList<>();

  private final EarsKnob[] knobs;
  private final EarsMachineAdapter<Ctx> machine;
  private final int[] configuration;

  public CompatNumberProblem(Knob[] knobs, EarsMachineAdapter<Ctx> machine) {
    this.knobs = Arrays.stream(knobs).map(EarsKnob::new).toArray(EarsKnob[]::new);
    this.machine = machine;
    this.configuration = new int[knobs.length];
  }

  @Override
  public EarsContext context() {
    return new EarsContext(knobs, configuration);
  }

  @Override
  public void update(EarsContext context, Map<String, Double> measurement) {}

  public void evaluate(List<Integer> solution) {
    for (int i = 0; i < solution.size(); i++) {
      configuration[i] = solution.get(i);
    }
    EarsContext context = context();
    update(context, machine.run(context));
  }

  public void makeFeasible(List<Integer> solution) {
    for (int i = 0; i < knobs.length; i++) {
      solution.set(i, knobs[i].constrainIndex(solution.get(i)));
    }
  }

  public boolean isFeasible(List<Integer> solution) {
    for (int i = 0; i < knobs.length; i++) {
      int x = solution.get(i);
      if (!(0 <= x && x < knobs[i].configurationCount())) {
        return false;
      }
    }
    return true;
  }

  public List<Integer> getRandomSolution() {
    return Arrays.stream(knobs).map(EarsKnob::randomIndex).collect(toList());
  }
}
