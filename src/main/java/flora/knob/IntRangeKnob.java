package flora.knob;

import flora.Knob;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link Knob} that can be set to a value within a range of integers. The range is inclusive on
 * both start and end.
 */
public final class IntRangeKnob implements Knob {
  private final int start;
  private final int end;
  private final int stepSize;

  private final AtomicInteger value;

  public IntRangeKnob(int start, int end, int stepSize) {
    if (stepSize < 0) {
      throw new IllegalArgumentException(
          String.format("The step size (%d) must be positive for an integer range.", stepSize));
    } else if (end <= start) {
      throw new IllegalArgumentException(
          String.format(
              "The end value (%d) must be greater than the start value (%d).", end, start));
    }
    this.start = start;
    this.end = end;
    this.stepSize = stepSize;
    this.value = new AtomicInteger(start);
  }

  public IntRangeKnob(int start, int end) {
    this(start, end, 1);
  }

  public IntRangeKnob(IntRangeKnob knob) {
    this(knob.getStart(), knob.getEnd(), knob.getStep());
  }

  /** Writes the knob as a json dict. */
  @Override
  public String toString() {
    return String.format(
        "{\"knob_type\":\"%s\",\"value\":%s,\"start\":%d,\"end\":%d,\"step\":%d",
        this.getClass(), this.value.get(), start, end, stepSize);
  }

  /** Returns the value. */
  @Override
  public final int getInt() {
    return this.value.get();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof IntRangeKnob) {
      IntRangeKnob other = (IntRangeKnob) o;
      return this.start == other.start
          && this.end == other.end
          && this.stepSize == other.stepSize
          && this.value.get() == other.value.get();
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int hash = 0;
    for (int i : new int[] {start, end, stepSize, value.get()}) {
      hash = Integer.hashCode(i) + prime * hash;
    }
    return hash;
  }

  /** Safely sets the value by checking if it is within the range and throws otherwise. */
  public void setValue(int value) {
    if (start <= value && value <= end && (value - start) % stepSize == 0) {
      this.value.set(value);
    } else {
      throw new IllegalArgumentException(
          String.format(
              "The value (%d) cannot be set to one outside of the range (%d, %d, %d).",
              value, this.start, this.end, this.stepSize));
    }
  }

  /** Returns the range start. */
  public int getStart() {
    return start;
  }

  /** Returns the range end. */
  public int getEnd() {
    return end;
  }

  /** Returns the distance between range steps. */
  public int getStep() {
    return stepSize;
  }
}
