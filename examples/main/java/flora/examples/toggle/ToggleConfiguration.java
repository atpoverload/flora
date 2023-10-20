package flora.examples.toggle;

import java.util.concurrent.ThreadLocalRandom;

/** Configuration for the {@link Toggle}. */
public record ToggleConfiguration(boolean toggle1, boolean toggle2)
    implements Comparable<ToggleConfiguration> {
  public static final ToggleConfiguration FALSE_FALSE = new ToggleConfiguration(false, false);
  public static final ToggleConfiguration TRUE_FALSE = new ToggleConfiguration(true, false);
  public static final ToggleConfiguration FALSE_TRUE = new ToggleConfiguration(false, true);
  public static final ToggleConfiguration TRUE_TRUE = new ToggleConfiguration(true, true);

  public static final ToggleConfiguration[] configurations() {
    return new ToggleConfiguration[] {FALSE_FALSE, TRUE_FALSE, FALSE_TRUE, TRUE_TRUE};
  }

  public static ToggleConfiguration randomConfiguration() {
    return new ToggleConfiguration(
        ThreadLocalRandom.current().nextBoolean(), ThreadLocalRandom.current().nextBoolean());
  }

  @Override
  public int compareTo(ToggleConfiguration other) {
    int firstCompare = Boolean.compare(toggle1, other.toggle1);
    switch (firstCompare) {
      case 0:
        return Boolean.compare(toggle2, other.toggle2);
      default:
        return firstCompare;
    }
  }
}
