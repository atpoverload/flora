package flora.examples.toggle;

import static org.junit.Assert.assertEquals;

import flora.strategy.archiving.RandomArchivingStrategy;
import java.util.Map;
import org.junit.Test;

public final class SummaryTest {
  @Test
  public void summaryTest() {
    RandomArchivingStrategy<?, ?, ToggleContext> strategy =
        new RandomArchivingStrategy<>(ToggleContext.DEFAULT_CONTEXT);
    ToggleMachine machine = new ToggleMachine();

    for (int i = 0; i < 100; i++) {
      ToggleContext context = strategy.context();
      strategy.update(context, machine.run(context));
    }

    Map<?, Map<String, Double>> means = strategy.summary().means();
    assertEquals(0.01, means.get(ToggleConfiguration.FALSE_FALSE).get("stopwatch"), 0.01);
    assertEquals(0.013, means.get(ToggleConfiguration.TRUE_FALSE).get("stopwatch"), 0.013);
    assertEquals(0.015, means.get(ToggleConfiguration.FALSE_TRUE).get("stopwatch"), 0.015);
    assertEquals(0.018, means.get(ToggleConfiguration.TRUE_TRUE).get("stopwatch"), 0.018);
  }
}
