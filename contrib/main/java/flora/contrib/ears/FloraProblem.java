package flora.contrib.ears;

import static java.util.stream.Collectors.toList;

import flora.Machine;
import flora.WorkFactory;
import flora.WorkUnit;
import flora.util.DataCollector;
import flora.util.LoggerUtil;
import java.time.Instant;
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

  private final WorkFactory<K, C, W> workFactory;
  private final Machine machine;
  private final DataCollector<Instant, C> collector = new DataCollector<>();

  private final int numberOfObjectives;
  private final double[] failureMeasurement;

  public FloraProblem(WorkFactory<K, C, W> workFactory, Machine machine) {
    super("sunflow-rendering", workFactory.knobCount(), 1, machine.meters().length, 0);
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

    this.numberOfObjectives = machine.meters().length;
    this.failureMeasurement =
        IntStream.range(/* start= */ 0, numberOfObjectives)
            .mapToDouble(i -> Double.MAX_VALUE - 1)
            .toArray();
  }

  @Override
  public void evaluate(NumberSolution<Double> solution) {
    Instant timestamp = Instant.now();
    if (!isFeasible(solution)) {
      makeFeasible(solution);
    }
    W work = workFactory.newWorkUnit(toIntArray(solution));
    collector.addConfiguration(timestamp, work.configuration());
    try {
      LoggerUtil.getLogger()
          .info(String.format("executing configuration %s", work.configuration()));
      Map<String, Double> measurement = machine.run(work);
      LoggerUtil.getLogger()
          .info(
              String.format(
                  "configuration %s had measurement %s", work.configuration(), measurement));
      collector.addMeasurement(timestamp, measurement);
      solution.setObjectives(measurement.values().stream().mapToDouble(d -> d).toArray());
    } catch (Exception error) {
      LoggerUtil.getLogger()
          .log(
              Level.INFO,
              String.format("an error occurred with configuration %s", work.configuration()),
              error);
      collector.addError(timestamp, error);
      solution.setObjectives(failureMeasurement);
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

  public DataCollector<Instant, C> getCollector() {
    return collector;
  }
}
