package flora.examples.toggle;

import flora.Knob;
import flora.util.LoggerUtil;
import flora.work.IndexableWorkUnit;
import flora.work.RandomizableWorkUnit;
import java.util.logging.Logger;

/** Work unit for the {@link ToggleMachine}. */
public record Toggle(ToggleConfiguration configuration)
    implements RandomizableWorkUnit<ToggleKnobs, ToggleConfiguration, Toggle>,
        IndexableWorkUnit<ToggleKnobs, ToggleConfiguration, Toggle> {
  public static final Toggle DEFAULT = Toggle.newFromBooleans(false, false);

  private static final Logger logger = LoggerUtil.getLogger();

  /** Generates a random {@code Toggle}, which is one of the four possible combinations. */
  public static Toggle randomConfiguration() {
    return Toggle.newFromConfiguration(ToggleConfiguration.randomConfiguration());
  }

  /** Builds a work unit from another configuration. */
  public static Toggle newFromConfiguration(ToggleConfiguration configuration) {
    return new Toggle(configuration);
  }

  /** Builds a work unit from two booleans. */
  public static Toggle newFromBooleans(boolean toggle1, boolean toggle2) {
    return new Toggle(new ToggleConfiguration(toggle1, toggle2));
  }

  /** Builds a work unit from two indicies. */
  public static Toggle newFromIndices(int toggle1, int toggle2) {
    ToggleKnobs knobs = ToggleKnobs.INSTANCE;
    return new Toggle(
        new ToggleConfiguration(
            knobs.toggle1().fromIndex(toggle1), knobs.toggle2().fromIndex(toggle2)));
  }

  /** Builds a work unit from an array. */
  public static Toggle newFromArray(int[] toggles) {
    return newFromIndices(toggles[0], toggles[1]);
  }

  @Override
  public ToggleKnobs knobs() {
    return ToggleKnobs.INSTANCE;
  }

  @Override
  public Toggle random() {
    return Toggle.randomConfiguration();
  }

  @Override
  public Toggle fromIndices(int[] indices) {
    return newFromArray(indices);
  }

  /** Sets a sleep time based on the toggles set and then sleeps. */
  @Override
  public void run() {
    long sleepTime = (long) (10 * (1 + Math.random()));
    if (configuration.toggle1()) sleepTime += 3 * (1 + Math.random());
    if (configuration.toggle2()) sleepTime += 5 * (1 + Math.random());
    if (configuration.toggle1() && configuration.toggle2()) sleepTime -= 12 * (1 + Math.random());
    try {
      logger.info(String.format("%s sleeps for %d ms", this.getClass().getSimpleName(), sleepTime));
      Thread.sleep(Math.max(1, sleepTime));
    } catch (Exception e) {

    }
  }
}
