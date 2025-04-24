package flora.contrib.ears;

import static java.util.stream.Collectors.toList;

import flora.Machine;
import flora.WorkFactory;
import flora.WorkUnit;
import flora.util.DataCollector;
import flora.util.LoggerUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.IntStream;
import org.um.feri.ears.problems.NumberProblem;
import org.um.feri.ears.problems.NumberSolution;

public final class FloraProblem<K, C, W extends WorkUnit<K, C>> extends NumberProblem<Double> {
  private static int[] toIntArray(NumberSolution<Double> solution) {
    return solution.getVariables().stream().mapToInt(Double::intValue).toArray();
  }

  private final String name;
  private final WorkFactory<K, C, W> workFactory;
  private final Machine machine;
  private final DataCollector<Integer, C> collector = new DataCollector<>();

  private final int numberOfObjectives;
  private final double[] failureMeasurement;

  private int iteration = 0;

  public FloraProblem(String name, WorkFactory<K, C, W> workFactory, Machine machine) {
    super(name, workFactory.knobCount(), 1, machine.meters().size(), 0);
    this.name = name;
    this.workFactory = workFactory;
    this.machine = machine;

    this.lowerLimit = new ArrayList<>();
    this.upperLimit = new ArrayList<>();

    for (int i = 0; i < workFactory.knobCount(); i++) {
      this.lowerLimit.add(0.0);
    }
    int[] configs = workFactory.configurationSize();
    for (int configCount : workFactory.configurationSize()) {
      this.upperLimit.add((double) configCount);
    }

    this.numberOfObjectives = machine.meters().size();
    this.failureMeasurement =
        IntStream.range(/* start= */ 0, numberOfObjectives)
            .mapToDouble(i -> Double.MAX_VALUE - 1)
            .toArray();
  }

  @Override
  public void evaluate(NumberSolution<Double> solution) {
    if (!isFeasible(solution)) {
      makeFeasible(solution);
    }
    LoggerUtil.getLogger()
        .info(String.format("[%s] iteration %d - trying solution %s", name, iteration, solution));
    W work = workFactory.newWorkUnit(toIntArray(solution));
    collector.addConfiguration(Integer.valueOf(iteration), work.configuration());
    try {
      LoggerUtil.getLogger()
          .info(
              String.format(
                  "[%s] iteration %d - trying configuration %s",
                  name, iteration, work.configuration()));
      Map<String, Double> measurement = machine.run(work);
      LoggerUtil.getLogger()
          .info(String.format("[%s] iteration %d - measured %s", name, iteration, measurement));
      collector.addMeasurement(Integer.valueOf(iteration), measurement);
      solution.setObjectives(measurement.values().stream().mapToDouble(d -> d).toArray());
    } catch (Exception error) {
      LoggerUtil.getLogger()
          .log(
              Level.INFO,
              String.format(
                  "[%s] iteration %d - an error occurred (%s)", name, iteration, error.toString()),
              error);
      error.printStackTrace();
      collector.addError(Integer.valueOf(iteration), error);
      solution.setObjectives(failureMeasurement);
    } finally {
      iteration++;
    }
  }

  @Override
  public boolean isFeasible(NumberSolution<Double> solution) {
    return workFactory.isValid(toIntArray(solution));
  }

  @Override
  public void makeFeasible(NumberSolution<Double> solution) {
    if (solution.getVariables().size() != workFactory.knobCount()) {
      NumberSolution<Double> newSolution = getRandomSolution();
      for (int i = 0; i < workFactory.knobCount(); i++) {
        solution.setValue(i, newSolution.getValue(i));
      }
      return;
    }
    // TODO: unnecessary sets; could check equality?
    int[] configuration = workFactory.fixConfiguration(toIntArray(solution));
    for (int i = 0; i < workFactory.knobCount(); i++) {
      solution.setValue(i, (double) configuration[i]);
    }
  }

  @Override
  public NumberSolution<Double> getRandomSolution() {
    return new NumberSolution<>(
        numberOfObjectives,
        Arrays.stream(workFactory.randomConfiguration())
            .mapToDouble(d -> d)
            .mapToObj(Double::valueOf)
            .collect(toList()));
  }

  public DataCollector<Integer, C> getCollector() {
    return collector;
  }
}
