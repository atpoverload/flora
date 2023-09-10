package flora;

import java.util.Map;

/** An interface for a measurable work unit. */
public interface Machine<K, C, KC extends KnobContext<K, C>> {
  /** Unit of work. */
  void runWorkload(KC context);

  /** Returns the meters used by the machine. */
  Map<String, Meter> meters();

  /** Returns the strategy used by the machine. */
  Strategy<K, C, KC> strategy();
}
