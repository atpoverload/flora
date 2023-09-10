package flora.examples.toggle;

import static java.util.stream.Collectors.joining;

import flora.machine.ComposedMachine;
import flora.meter.Stopwatch;
import flora.strategy.ArchivingStrategy;
import flora.strategy.ArchivingStrategy.ArchiveRecordSummary;
import flora.strategy.RandomArchivingStrategy;
import java.util.Map;

/** A simple example for a {@link Machine} that adjusts toggles. */
final class ToggleMachine extends ComposedMachine<ToggleKnobs, ToggleConfiguration, ToggleContext> {
  private ToggleMachine() {
    super(
        Map.of("stopwatch1", new Stopwatch(), "stopwatch2", new Stopwatch()),
        new RandomArchivingStrategy<>(ToggleContext.random()));
  }

  @Override
  public void runWorkload(ToggleContext context) {
    long sleepTime = (long) (10 * (1 + Math.random()));
    if (context.configuration().toggle1()) sleepTime += 3 * (1 + Math.random());
    if (context.configuration().toggle2()) sleepTime += 5 * (1 + Math.random());
    try {
      Thread.sleep(sleepTime);
    } catch (Exception e) {

    }
  }

  @Override
  public String toString() {
    ArchiveRecordSummary<ToggleKnobs, ToggleConfiguration, ToggleContext> summary = summary();
    return summary.means().keySet().stream()
        .sorted()
        .flatMap(
            configuration ->
                summary.means().get(configuration).keySet().stream()
                    .map(
                        measure ->
                            String.format(
                                "%-49s : %s = %.6f +/- %.6f (%d)",
                                configuration,
                                measure,
                                summary.means().get(configuration).get(measure),
                                summary.deviations().get(configuration).get(measure),
                                summary.counts().get(configuration).get(measure))))
        .collect(joining("\n"));
  }

  private ArchiveRecordSummary<ToggleKnobs, ToggleConfiguration, ToggleContext> summary() {
    return ((ArchivingStrategy<ToggleKnobs, ToggleConfiguration, ToggleContext>) strategy())
        .summary();
  }

  public static void main(String[] args) {
    ToggleMachine machine = new ToggleMachine();
    for (int i = 0; i < 10; i++) machine.run();
    System.out.println(machine.strategy());
    System.out.println(machine);
  }
}
