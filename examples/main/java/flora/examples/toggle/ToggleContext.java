package flora.examples.toggle;

import flora.strategy.mab.MultiArmedBandit;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Context for the {@link ToggleMachine}. */
public final class ToggleContext
    extends MultiArmedBandit<ToggleKnobs, ToggleConfiguration, ToggleContext> {
  public static final ToggleContext DEFAULT_CONTEXT = ToggleContext.fromBooleans(false, false);

  public static ToggleContext randomContext() {
    return ToggleContext.fromBooleans(
        ThreadLocalRandom.current().nextBoolean(), ThreadLocalRandom.current().nextBoolean());
  }

  /** Builds a context from another configuration. */
  public static ToggleContext fromConfiguration(ToggleConfiguration configuration) {
    return new ToggleContext(configuration);
  }

  /** Builds a context from two booleans. */
  public static ToggleContext fromBooleans(boolean toggle1, boolean toggle2) {
    return new ToggleContext(new ToggleConfiguration(toggle1, toggle2));
  }

  /** Builds a context from two indicies. */
  public static ToggleContext fromIndicies(int toggle1, int toggle2) {
    ToggleKnobs knobs = ToggleKnobs.instance();
    return new ToggleContext(
        new ToggleConfiguration(
            knobs.toggle1().fromIndex(toggle1), knobs.toggle2().fromIndex(toggle2)));
  }

  private ToggleContext(ToggleConfiguration configuration) {
    // TODO: this is a hack
    withConfiguration(configuration);
  }

  @Override
  public ToggleKnobs knobs() {
    return ToggleKnobs.instance();
  }

  @Override
  public ToggleContext random() {
    return ToggleContext.randomContext();
  }

  @Override
  protected double reward(ToggleContext context, Map<String, Double> measurements) {
    return measurements.get("stopwatch");
  }
}
