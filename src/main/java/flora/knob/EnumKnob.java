package flora.knob;

import static java.util.stream.Collectors.toList;

import flora.Knob;
import java.util.Arrays;
import java.util.List;

/** A {@link Knob} that represents an enum type. */
// TODO: since enums are compile-time defined, this is wasteful
public final class EnumKnob<E extends Enum<E>> implements Knob {
  private final Class<E> cls;
  private final List<E> values;

  public EnumKnob(Class<E> cls) {
    this.cls = cls;
    this.values = Arrays.stream(this.cls.getEnumConstants()).map(e -> (E) e).collect(toList());
  }

  /** Returns the cardinality of the enum. */
  @Override
  public int configurationCount() {
    return values.size();
  }

  /** Returns the enum value of ordinality {@code index} if it is in range, and throws otherwise. */
  @Override
  @SuppressWarnings("unchecked")
  public <T extends Object> T fromIndex(int index, Class<T> cls) {
    if (cls.equals(this.cls)) {
      return (T) fromIndex(index);
    }
    throw new KnobValueException(this, cls, index);
  }

  /** Returns the enum value of ordinality {@code index} if it is in range, and throws otherwise. */
  public E fromIndex(int index) {
    if (0 <= index && index < configurationCount()) {
      return values.get(index);
    }
    throw new KnobValueException(this, this.cls, index);
  }
}
