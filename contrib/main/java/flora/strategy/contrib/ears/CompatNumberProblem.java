package flora.strategy.contrib.ears;

import flora.Machine;
import flora.WorkUnit;
import flora.knob.meta.ConstrainedKnob;
import flora.knob.meta.RandomizableKnob;
import java.util.ArrayList;
import java.util.Map;
import org.um.feri.ears.problems.NumberProblem;
import org.um.feri.ears.problems.NumberSolution;

public final class CompatNumberProblem<K extends ConstrainedKnob & RandomizableKnob>
    extends NumberProblem<Double> {
  private final RawWorkFactory<K, ?> workFactory;
  private final Machine machine;
  private final K[] knobs;
  private final int numberOfObjectives;

  public CompatNumberProblem(String name, RawWorkFactory<K, ?> workFactory, Machine machine) {
    super(name, workFactory.knobs().length, 1, machine.meters().size(), 0);
    this.workFactory = workFactory;
    this.machine = machine;

    this.knobs = workFactory.knobs();
    this.numberOfObjectives = machine.meters().size();
    this.lowerLimit = new ArrayList<>();
    this.upperLimit = new ArrayList<>();
    for (K knob : knobs) {
      this.lowerLimit.add(0.0);
      this.upperLimit.add((double) knob.configurationCount());
    }
  }

  @Override
  public void evaluate(NumberSolution<Double> solution) {
    WorkUnit<?, ?> work =
        workFactory.fromIndices(
            solution.getVariables().stream().mapToInt(Double::intValue).toArray());
    try {
      Map<String, Double> measurement = machine.run(work);
      solution.setObjectives(measurement.values().stream().mapToDouble(d -> d).toArray());
    } catch (Exception e) {
      solution.setObjectives(new double[] {Double.MAX_VALUE, Double.MAX_VALUE});
    }
  }

  @Override
  public void makeFeasible(NumberSolution<Double> solution) {
    for (int i = 0; i < knobs.length; i++) {
      solution.setValue(i, (double) knobs[i].constrain(solution.getValue(i).intValue()));
    }
  }

  @Override
  public boolean isFeasible(NumberSolution<Double> solution) {
    for (int i = 0; i < knobs.length; i++) {
      if (!knobs[i].isValid(solution.getValue(i).intValue())) {
        return false;
      }
    }
    return true;
  }

  @Override
  public NumberSolution<Double> getRandomSolution() {
    final var solution = new ArrayList<Double>();
    for (K knob : knobs) {
      solution.add((double) knob.random());
    }
    return new NumberSolution<>(numberOfObjectives, solution);
  }
}
