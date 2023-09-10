package flora;

import java.util.Map;

/** An interface that provides and consumes contexts. */
public interface Strategy<K, C, KC extends KnobContext<K, C>> {
  /** Returns the {@link KnobContext} of the strategy. */
  KC context();

  /** Feeds a {@link KnobContext} and measurement to the strategy. */
  void update(KC context, Map<String, Double> measurement);
}
