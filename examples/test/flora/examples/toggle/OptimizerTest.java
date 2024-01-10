package flora.examples.toggle;

import static org.junit.Assert.assertTrue;

import flora.strategy.mab.MultiArmedBanditStrategy;
import flora.strategy.mab.epsilon.RandomEpsilonPolicy;
import flora.strategy.mab.exploit.LowestConfiguration;
import java.time.Duration;
import java.time.Instant;
import org.junit.Test;

public final class OptimizerTest {
  @Test
  public void optimizerTest() {
    MultiArmedBanditStrategy<?, ?, ToggleContext> strategy =
        new MultiArmedBanditStrategy<>(
            ToggleContext.DEFAULT_CONTEXT,
            new RandomEpsilonPolicy(0.1, "decreasing"),
            LowestConfiguration.instance());
    ToggleMachine machine = new ToggleMachine();

    Instant start = Instant.now();
    while (Duration.between(start, Instant.now()).toSeconds() < 30) {
      ToggleContext context = strategy.context();
      strategy.update(context, machine.run(context));
    }

    ToggleContext context = LowestConfiguration.instance().exploit(strategy.context());
    for (ToggleConfiguration config : context.rewardedConfigurations()) {
      assertTrue(context.rewardedCount(config) <= context.rewardedCount(context.configuration()));
      assertTrue(context.configuration().compareTo(config) < 1);
    }
  }
}
