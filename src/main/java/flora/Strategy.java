package flora;

import java.util.Map;

/** An interface that provides and consumes contexts. */
public interface Strategy<K, C, W extends WorkUnit<K, C>> {
  /** Returns the {@link KnobContext} of the strategy. */
  W nextWorkload();

  /** Feeds a {@link KnobContext} and measurement to the strategy. */
  void update(W workload, Map<String, Double> measurement);
}
