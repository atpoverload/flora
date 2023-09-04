package flora.knob;

import flora.Knob;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** A {@link Knob} that contains an enum of some type. */
public final class EnumKnob<T extends Enum<T>> implements Knob {
  // temporary storage for already encountered enum names
  // may break if too many enums are accessed but better than making deep copies
  private static final HashMap<Class<?>, String[]> NAMES = new HashMap<>();

  private static String[] getNames(Class<?> cls) {
    synchronized (NAMES) {
      if (!NAMES.containsKey(cls)) {
        NAMES.put(
            cls,
            Arrays.stream(cls.getEnumConstants())
                .map(e -> ((Enum<?>) e).name())
                .toArray(String[]::new));
      }
    }
    return NAMES.get(cls);
  }

  private final AtomicReference<T> value = new AtomicReference<>();
  private final AtomicInteger index = new AtomicInteger(-1);

  private String[] names = new String[0];

  public EnumKnob(T value) {
    this.value.set(value);
    this.index.set(value.ordinal());
  }

  public EnumKnob(EnumKnob<T> knob) {
    this.value.set(knob.value.get());
    this.index.set(knob.index.get());
  }

  /** Writes the knob as a json dict. */
  @Override
  public String toString() {
    return String.format("{\"knob_type\":\"%s\",\"value\":%s", this.getClass(), this.value.get());
  }

  /** Retrieves the index of the current value. */
  @Override
  public int getInt() {
    if (this.index.get() > -1) {
      return this.index.get();
    } else {
      throw new IllegalArgumentException("No value was set for this knob.");
    }
  }

  /** Retrieves the value if the type matches and throws otherwise. */
  @Override
  public String getEnum(Class<? extends Enum<?>> cls) {
    if (this.value.get().getClass().equals(cls)) {
      return this.value.get().name();
    } else {
      throw new IllegalArgumentException(String.format("This knob does not have a %s enum.", cls));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof EnumKnob) {
      EnumKnob<?> other = (EnumKnob<?>) o;
      return this.getClass().equals(other.getClass()) && this.value.get().equals(other.value.get());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.value.get().hashCode();
  }

  /** Sets the value. */
  public void setValue(T value) {
    this.value.set(value);
    this.index.set(value.ordinal());
  }

  /** Safely sets the value by checking if it is in the enum names and throws otherwise. */
  public void setValue(String value) {
    if (Arrays.stream(getNames()).anyMatch(value::equals)) {
      // TODO: this should be a guarantee
      this.value.set((T) Enum.valueOf(this.value.get().getClass(), value));
      this.index.set(Arrays.asList(getNames()).indexOf(value));
    } else {
      throw new IllegalArgumentException(
          String.format(
              "Expected one of %s for %s but got '%s'.",
              Arrays.toString(getNames()), this.value.getClass(), value));
    }
  }

  /** Retrieves the names for the stored enum's type. */
  public String[] getNames() {
    synchronized (this.names) {
      if (this.names.length == 0) {
        this.names = getNames(this.value.get().getClass());
      }
    }
    return Arrays.copyOf(this.names, this.names.length);
  }
}
