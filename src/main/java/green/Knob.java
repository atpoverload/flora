package green;

/** An interface that represents a configurable property. */
public interface Knob {
  /** Returns a boolean if one exists, and throw otherwise. */
  default boolean getBoolean() {
    throw new IllegalArgumentException("This knob does not have a boolean.");
  }

  /** Returns an int if one exists, and throw otherwise. */
  default int getInt() {
    throw new IllegalArgumentException("This knob does not have an int.");
  }

  /** Returns the name of a provided enum type if one exists, and throw otherwise. */
  default String getEnum(Class<? extends Enum<?>> cls) {
    throw new IllegalArgumentException(String.format("This knob does not have a %s enum.", cls));
  }
}
