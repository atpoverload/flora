package flora.strategy.contrib.ears;

import flora.Machine;
import flora.knob.meta.ConstrainedKnob;
import flora.knob.meta.RandomizableKnob;
import flora.work.IndexableWorkUnit;
import java.util.ArrayList;
import java.util.Map;
import org.um.feri.ears.problems.NumberProblem;
import org.um.feri.ears.problems.NumberSolution;

public final class CompatNumberProblem<K extends ConstrainedKnob & RandomizableKnob>
    extends NumberProblem<Double> {
  private final K[] knobs;
  private final IndexableWorkUnit<?, ?, ?> workUnit;
  private final Machine machine;
  private final int numberOfObjectives;

  public CompatNumberProblem(
      String name, K[] knobs, IndexableWorkUnit<?, ?, ?> workUnit, Machine machine) {
    super(name, knobs.length, 1, machine.meters().size(), 0);
    this.knobs = knobs;
    this.workUnit = workUnit;
    this.machine = machine;

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
    IndexableWorkUnit<?, ?, ?> work =
        workUnit.fromIndices(solution.getVariables().stream().mapToInt(Double::intValue).toArray());
    Map<String, Double> measurement = machine.run(work);
    solution.setObjectives(measurement.values().stream().mapToDouble(d -> d).toArray());
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
