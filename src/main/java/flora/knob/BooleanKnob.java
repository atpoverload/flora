package flora.knob;

import flora.Knob;

/** A {@link Knob} that represents a boolean. */
public final class BooleanKnob implements Knob {
  private static final BooleanKnob instance = new BooleanKnob();

  /** Returns a cached instance to prevent unnecessary copying. */
  public static BooleanKnob instance() {
    return instance;
  }

  private BooleanKnob() {}

  /** Returns the number of possible booleans, which is two (true or false). */
  @Override
  public int configurationCount() {
    return 2;
  }

  /** Checks for a boolean and grabs it with {@code fromIndex}, otherwise throw. */
  @Override
  @SuppressWarnings("unchecked")
  public <T extends Object> T fromIndex(int index, Class<T> cls) {
    if (cls.equals(Boolean.class)) {
      return (T) Boolean.valueOf(fromIndex(index));
    }
    throw new KnobValueException(this, cls, index);
  }

  /** Returns false/true if 0/1 is provided, otherwise throw. */
  public boolean fromIndex(int index) {
    switch (index) {
      case 0:
        return false;
      case 1:
        return true;
      default:
        throw new KnobValueException(this, Boolean.class, index);
    }
  }
}
