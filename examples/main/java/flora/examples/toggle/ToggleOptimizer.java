package flora.examples.toggle;

import flora.strategy.mab.MultiArmedBanditStrategy;
import flora.strategy.mab.epsilon.RandomEpsilonPolicy;
import flora.strategy.mab.exploit.LowestConfiguration;
import java.time.Duration;
import java.time.Instant;

public final class ToggleOptimizer {
  public static void main(String[] args) {
    MultiArmedBanditStrategy<?, ?, ToggleContext> strategy =
        new MultiArmedBanditStrategy<>(
            ToggleContext.defaultContext(),
            new RandomEpsilonPolicy(0.1, "decreasing"),
            LowestConfiguration.instance());
    ToggleMachine machine = new ToggleMachine();

    Instant start = Instant.now();
    while (Duration.between(start, Instant.now()).toSeconds() < 30) {
      ToggleContext context = strategy.context();
      strategy.update(context, machine.run(context));
    }

    System.out.println(strategy.context());
  }
}
