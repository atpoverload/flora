package flora.machine;

import flora.KnobContext;
import flora.Machine;

/** A {@link Machine} which will automatically meter the workload and update the strategy. */
public abstract class RunnableMachine<K, C, KC extends KnobContext<K, C>>
    implements Machine<K, C, KC>, Runnable {
  /**
   * Executes {@link runWorkload} with the {@link strategy} context and {@link meters} running. The
   * context and measurement are fed back to the {@link strategy}.
   */
  @Override
  public final void run() {
    MachineRunner.run(this);
  }
}
