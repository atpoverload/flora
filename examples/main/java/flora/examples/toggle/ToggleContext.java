package flora.examples.toggle;

import flora.context.RandomizableContext;
import java.util.concurrent.ThreadLocalRandom;

/** Context for the {@link ToggleMachine}. */
record ToggleContext(ToggleKnobs knobs, ToggleConfiguration configuration)
    implements RandomizableContext<ToggleKnobs, ToggleConfiguration, ToggleContext> {
  public static ToggleContext random() {
    return new ToggleContext(
        ToggleKnobs.instance(),
        new ToggleConfiguration(
            ThreadLocalRandom.current().nextBoolean(), ThreadLocalRandom.current().nextBoolean()));
  }

  public static ToggleContext of(ToggleConfiguration configuration) {
    return new ToggleContext(ToggleKnobs.instance(), configuration);
  }

  @Override
  public ToggleContext randomize() {
    return random();
  }
}
