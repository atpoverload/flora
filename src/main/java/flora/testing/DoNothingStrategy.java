package flora.testing;

import flora.Knob;
import flora.Strategy;
import java.util.Map;

/** A {@link Strategy} that does nothing when called. */
public final class DoNothingStrategy implements Strategy {
  private static final DoNothingStrategy instance = new DoNothingStrategy();

  /** Returns a cached instance to prevent unnecessary copying. */
  public static DoNothingStrategy getInstance() {
    return instance;
  }

  /** Returns an empty configuration. */
  @Override
  public Map<String, Knob> nextConfiguration() {
    return Map.of();
  }

  /** No-op implementation */
  @Override
  public void update(Map<String, Knob> knobs, Map<String, Double> measurement) {}

  private DoNothingStrategy() {}
}
