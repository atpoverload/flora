package flora.experiments.sunflow;

import flora.meter.SnapshotMeter;
import java.util.concurrent.atomic.AtomicBoolean;

/** A {@link SnapshotMeter} that wraps around an atomic boolean. */
public final class KillSwitchMeter extends SnapshotMeter {
  private final AtomicBoolean killSwitch;

  public KillSwitchMeter(AtomicBoolean killSwitch) {
    this.killSwitch = killSwitch;
  }

  /** Returns the state of the switch as 0 for off and 1 for on. */
  @Override
  public double read() {
    return killSwitch.get() ? 1 : 0;
  }
}
