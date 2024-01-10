package flora.knob;

import flora.Knob;
import java.util.Arrays;

/** A {@link Knob} that represents a collection of ints. */
public final class IntCollectionKnob implements IntKnob {
  /** Creates an {@code IntCollectionKnob} from the given numbers. */
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

  public IntCollectionKnob(int[] values) {
    if (values.length < 2) {
      throw new IllegalArgumentException(
          String.format("There are insufficient knob values (%s).", Arrays.toString(values)));
    }
    this.values = Arrays.copyOf(values, values.length);
  }

  /** Returns the number of values in the collection. */
  @Override
  public int configurationCount() {
    return values.length;
  }

  /** Returns the value at {@code index} if it is in range, and throws otherwise. */
  @Override
  @SuppressWarnings("unchecked")
  public <T extends Object> T fromIndex(int index, Class<T> cls) {
    if (cls.equals(Integer.class)) {
      return (T) Integer.valueOf(fromIndex(index));
    }
    throw new KnobValueException(this, cls, index);
  }

  /** Returns the value at the index if it's in the value range, otherwise throw. */
  @Override
  public int fromIndex(int index) {
    if (0 <= index && index < configurationCount()) {
      return values[index];
    }
    throw new KnobValueException(this, Integer.class, index);
  }

  /** Returns a defensive copy of the values. */
  public int[] values() {
    return Arrays.copyOf(values, configurationCount());
  }
}
