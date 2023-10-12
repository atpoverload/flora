package flora;

import java.util.Map;

/** An interface that provides and consumes contexts. */
public interface Strategy<K, C, Ctx extends WorkloadContext<K, C>> {
  /** Returns the {@link KnobContext} of the strategy. */
  Ctx context();

  /** Feeds a {@link KnobContext} and measurement to the strategy. */
  void update(Ctx context, Map<String, Double> measurement);
}
