package flora.machine;

import flora.KnobContext;
import flora.Meter;
import flora.Strategy;
import java.util.HashMap;
import java.util.Map;

/** A {@link RunnableMachine} composed from a meter and a strategy. */
public abstract class ComposedMachine<K, C, KC extends KnobContext<K, C>>
    extends RunnableMachine<K, C, KC> {
  private final HashMap<String, Meter> meters = new HashMap<>();
  private final Strategy<K, C, KC> strategy;

  public ComposedMachine(Map<String, Meter> meters, Strategy<K, C, KC> strategy) {
    meters.forEach(this.meters::put);
    this.strategy = strategy;
  }

  /** Returns the underlying meter. */
  @Override
  public final Map<String, Meter> meters() {
    return new HashMap<>(this.meters);
  }

  /** Returns the underlying strategy. */
  @Override
  public final Strategy<K, C, KC> strategy() {
    return this.strategy;
  }
}
