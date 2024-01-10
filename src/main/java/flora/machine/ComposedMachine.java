package flora.machine;

import flora.Machine;
import flora.Meter;
import flora.WorkloadContext;
import java.util.HashMap;
import java.util.Map;

/** A {@link RunnableMachine} composed from a meter and a strategy. */
public abstract class ComposedMachine<K, C, Ctx extends WorkloadContext<K, C>>
    extends Machine<K, C, Ctx> {
  private final HashMap<String, Meter> meters = new HashMap<>();

  public ComposedMachine(Map<String, Meter> meters) {
    meters.forEach(this.meters::put);
  }

  /** Returns the underlying meter. */
  @Override
  public final Map<String, Meter> meters() {
    return new HashMap<>(this.meters);
  }
}
