package flora.knob;

import flora.Knob;

/** A {@link Knob} that represents a boolean. */
public final class BooleanKnob implements Knob {
  /** A cached instance to prevent unnecessary copying. */
  public static final BooleanKnob INSTANCE = new BooleanKnob();

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

  @Override
  public String toString() {
    return this.getClass().getName();
  }

  /** Do not instantiate outside of the singleton. */
  private BooleanKnob() {}
}
