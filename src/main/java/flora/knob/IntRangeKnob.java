package flora.knob;

import flora.Knob;

/** A {@link Knob} that represents an inclusive range of integers. */
public final class IntRangeKnob implements IntKnob {
  private final int start;
  private final int end;
  private final int step;
  private final int configCount;

  public IntRangeKnob(int start, int end, int step) {
    if (step < 0) {
      throw new IllegalArgumentException(
          String.format("The step size (%d) must be positive for an integer range.", step));
    } else if (end <= start) {
      throw new IllegalArgumentException(
          String.format(
              "The end value (%d) must be greater than the start value (%d).", end, start));
    }
    this.start = start;
    this.end = end;
    this.step = step;
    this.configCount = (end - start + 1) / step;
  }

  public IntRangeKnob(int start, int end) {
    this(start, end, 1);
  }

  /** Returns the number of values in the range. */
  @Override
  public int configurationCount() {
    return configCount;
  }

  /** Returns the index for {@code value} if it's in range, and throws otherwise. */
  public int toIndex(int value) {
    if (value < start || end < value) {
      throw new KnobIndexException(this, value);
    }
    double idx = (double) (value - start) / step;
    if (idx > Math.floor(idx)) {
      throw new KnobIndexException(this, value);
    }
    return (int) idx;
  }

  /** Returns the value for {@code index} if it's in range, and throws otherwise. */
  @Override
  @SuppressWarnings("unchecked")
  public <T extends Object> T fromIndex(int index, Class<T> cls) {
    if (cls.equals(Integer.class)) {
      return (T) Integer.valueOf(fromIndex(index));
    }
    throw new KnobValueException(this, cls, index);
  }

  /** Returns the range value if it's inbounds, otherwise throw. */
  @Override
  public int fromIndex(int index) {
    if (0 <= index && index < configurationCount()) {
      return start + index * step;
    }
    throw new KnobValueException(this, Integer.class, index);
  }

  /** Returns the range start. */
  public int start() {
    return start;
  }

  /** Returns the range end. */
  public int end() {
    return end;
  }

  /** Returns the distance between range steps. */
  public int step() {
    return step;
  }
}
