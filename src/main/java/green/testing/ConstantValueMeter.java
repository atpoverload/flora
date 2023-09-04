package green.testing;

import green.meter.SnapshotMeter;
import java.util.concurrent.atomic.AtomicLong;

/** A {@link SnapshotMeter} that always returns the same value. */
public final class ConstantValueMeter extends SnapshotMeter {
  private final AtomicLong value = new AtomicLong(0);

  public ConstantValueMeter(double value) {
    this.value.set(Double.doubleToLongBits(value));
  }

  @Override
  public double read() {
    return Double.longBitsToDouble(this.value.get());
  }

  public void setValue(double value) {
    this.value.set(Double.doubleToLongBits(value));
  }
}
