package flora.meter;

import flora.Meter;

/** A {@link Meter} that only implements {@link Meter.read}. */
public abstract class SnapshotMeter implements Meter {
  @Override
  public final void start() {}

  @Override
  public final void stop() {}
}
