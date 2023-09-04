package green.knob;

import green.Knob;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/** A {@link Knob} that can be set to a collection of integer values. */
public final class IntCollectionKnob implements Knob {
  /** Constructs an {@code IntCollectionKnob} from the given numbers. */
  public IntCollectionKnob of(int first, int second, int... others) {
    int[] values = new int[2 + others.length];
    values[0] = first;
    values[1] = second;
    for (int i = 0; i < others.length; i++) {
      values[i + 2] = others[i];
    }
    return new IntCollectionKnob(values);
  }

  private final int[] values;
  private final AtomicInteger value;

  public IntCollectionKnob(int[] values) {
    if (values.length < 2) {
      throw new IllegalArgumentException(
          String.format("There are insufficient knob values (%s).", Arrays.toString(values)));
    }
    this.values = Arrays.copyOf(values, values.length);
    this.value = new AtomicInteger(this.values[0]);
  }

  public IntCollectionKnob(IntCollectionKnob other) {
    this(other.values);
  }

  /** Writes the knob as a json dict. */
  @Override
  public String toString() {
    return String.format(
        "{\"knob_type\":\"%s\",\"value\":%s,\"values\":%s",
        this.getClass(), this.value.get(), Arrays.toString(values));
  }

  /** Returns the value. */
  @Override
  public final int getInt() {
    return this.value.get();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof IntCollectionKnob) {
      IntCollectionKnob other = (IntCollectionKnob) o;
      return Arrays.equals(this.values, other.values) && this.value.get() == other.value.get();
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    return Arrays.hashCode(this.values) + prime * Integer.hashCode(this.value.get());
  }

  /** Safely sets the value by checking if it is part of the collection and throws otherwise. */
  public void setValue(int value) {
    if (Arrays.stream(values).anyMatch(v -> v == value)) {
      this.value.set(value);
    } else {
      throw new IllegalArgumentException(
          String.format(
              "The value (%d) cannot be set to one outside of the values %s.",
              value, Arrays.toString(this.values)));
    }
  }

  /** Returns a copy of the allowed collection. */
  public int[] getValues() {
    return Arrays.copyOf(this.values, this.values.length);
  }
}
