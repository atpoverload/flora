package green;

import java.util.Map;

/** An interface that can evaluate and recommend configurations.  */
public interface Strategy {
  /** Returns the knob configuration recommended by the strategy. */
  Map<String, Knob> nextConfiguration();

  /** Feeds a configuration and measurement to the strategy. */
  void update(Map<String, Knob> knobs, Map<String, Double> measurement);
}
