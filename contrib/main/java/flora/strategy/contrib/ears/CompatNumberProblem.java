package flora.strategy.contrib.ears;

import static java.util.stream.Collectors.toList;

import flora.Machine;
import flora.WorkUnit;
import flora.work.EncodedWorkFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.um.feri.ears.problems.NumberProblem;
import org.um.feri.ears.problems.NumberSolution;

public final class CompatNumberProblem extends NumberProblem<Double> {
  private final EncodedWorkFactory<?, ?, ?> workFactory;
  private final Machine machine;
  private final int numberOfObjectives;
  private final double[] failureMeasurement;

  public CompatNumberProblem(
      String name, EncodedWorkFactory<?, ?, ?> workFactory, Machine machine) {
    super(name, workFactory.knobCount(), 1, machine.meters().size(), 0);
    this.workFactory = workFactory;
    this.machine = machine;

    this.lowerLimit = new ArrayList<>();
    this.upperLimit = new ArrayList<>();

    for (int i = 0; i < workFactory.knobCount(); i++) {
      this.lowerLimit.add(0.0);
      this.upperLimit.add((double) workFactory.configurationCount(i));
    }

    this.numberOfObjectives = machine.meters().size();
    this.failureMeasurement =
        IntStream.range(0, machine.meters().size())
            .mapToDouble(i -> Double.MAX_VALUE - 1)
            .toArray();
  }

  @Override
  public void evaluate(NumberSolution<Double> solution) {
    int[] configuration = solution.getVariables().stream().mapToInt(Double::intValue).toArray();
    WorkUnit<?, ?> work = workFactory.newWorkUnit(configuration);
    try {
      System.out.println(work.configuration());
      Map<String, Double> measurement = machine.run(work);
      double[] measures = measurement.values().stream().mapToDouble(d -> d).toArray();
      System.out.println(Arrays.toString(measures));
      solution.setObjectives(measures);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(Arrays.toString(failureMeasurement));
      solution.setObjectives(failureMeasurement);
    }
  }

  @Override
  public void makeFeasible(NumberSolution<Double> solution) {
    int[] configuration = solution.getVariables().stream().mapToInt(Double::intValue).toArray();
    double[] repairedConfiguration =
        Arrays.stream(workFactory.repairConfiguration(configuration)).mapToDouble(d -> d).toArray();
    for (int i = 0; i < repairedConfiguration.length; i++) {
      solution.setValue(i, repairedConfiguration[i]);
    }
  }

  @Override
  public boolean isFeasible(NumberSolution<Double> solution) {
    int[] configuration = solution.getVariables().stream().mapToInt(Double::intValue).toArray();
    return workFactory.isValidConfiguration(configuration);
  }

  @Override
  public NumberSolution<Double> getRandomSolution() {
    List<Double> configuration =
        Arrays.stream(workFactory.randomConfiguration())
            .mapToDouble(d -> d)
            .mapToObj(Double::valueOf)
            .collect(toList());
    return new NumberSolution<>(numberOfObjectives, configuration);
  }
}
