package flora.contrib.ears;

import static java.util.stream.Collectors.toList;

import flora.MeteringMachine;
import flora.WorkFactory;
import flora.WorkUnit;
import flora.util.DataCollector;
import flora.util.LoggerUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.IntStream;
import org.um.feri.ears.problems.NumberProblem;
import org.um.feri.ears.problems.NumberSolution;

public final class FloraReplayProblem<K, C, W extends WorkUnit<K, C>>
    extends NumberProblem<Double> {
  private static int[] toIntArray(NumberSolution<Double> solution) {
    return solution.getVariables().stream().mapToInt(Double::intValue).toArray();
  }

  private final WorkFactory<K, C, W> workFactory;
  private final MeteringMachine machine;
  private final DataCollector<Integer, C> collector = new DataCollector<>();
  private final List<int[]> configurations;

  private final int numberOfObjectives;
  private final double[] failureMeasurement;

  private int iteration = 0;
  private int seedCount = 0;

  public FloraReplayProblem(
      String name,
      WorkFactory<K, C, W> workFactory,
      MeteringMachine machine,
      List<int[]> configurations) {
    super(name, workFactory.knobCount(), 1, machine.meters().length, 0);
    this.workFactory = workFactory;
    this.machine = machine;
    this.configurations = configurations;

    this.lowerLimit = new ArrayList<>();
    this.upperLimit = new ArrayList<>();

    for (int i = 0; i < workFactory.knobCount(); i++) {
      this.lowerLimit.add(0.0);
    }
    int[] configs = workFactory.configurationSize();
    for (int configCount : workFactory.configurationSize()) {
      this.upperLimit.add((double) configCount);
    }

    this.numberOfObjectives = machine.meters().length;
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
    W work = workFactory.newWorkUnit(toIntArray(solution));
    collector.addConfiguration(Integer.valueOf(iteration), work.configuration());
    try {
      LoggerUtil.getLogger().info(String.format("trying configuration %s", work.configuration()));
      Map<String, Double> measurement = machine.run(work);
      LoggerUtil.getLogger()
          .info(
              String.format("measured %s for configuration %s", measurement, work.configuration()));
      collector.addMeasurement(Integer.valueOf(iteration), measurement);
      solution.setObjectives(measurement.values().stream().mapToDouble(d -> d).toArray());
    } catch (Exception error) {
      LoggerUtil.getLogger()
          .log(
              Level.INFO,
              String.format("an error occurred for configuration %s", work.configuration()),
              error);
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
    if (configurations.size() < seedCount) {
      return new NumberSolution<>(
          numberOfObjectives,
          Arrays.stream(workFactory.randomConfiguration())
              .mapToDouble(d -> d)
              .mapToObj(Double::valueOf)
              .collect(toList()));
    } else {
      return new NumberSolution<>(
          numberOfObjectives,
          // TODO: getting the configurations in the order they were provided
          Arrays.stream(configurations.get(seedCount++ % configurations.size()))
              .mapToDouble(d -> d)
              .mapToObj(Double::valueOf)
              .collect(toList()));
    }
  }

  public DataCollector<Integer, C> getCollector() {
    return collector;
  }
}
