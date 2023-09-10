package flora;

/** An interface that represents a configurable property. */
public interface Knob {
  /** Returns the number of possible configurations for this knob. */
  int configurationCount();

  /** Retrieves a value of the given {@code cls} at some {@code index}, assuming both are valid. */
  <T extends Object> T fromIndex(int index, Class<T> cls);
}
