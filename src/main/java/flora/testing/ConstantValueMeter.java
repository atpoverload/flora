package flora.testing;

import flora.meter.SnapshotMeter;

/** A {@link SnapshotMeter} that always returns the same value. */
public class ConstantValueMeter extends SnapshotMeter {
  private double value;

  public ConstantValueMeter() {
    this(0);
  }

  public ConstantValueMeter(double value) {
    this.value = value;
  }

  @Override
  public double read() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }
}
