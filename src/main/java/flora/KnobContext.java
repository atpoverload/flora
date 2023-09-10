package flora;

/** An interface that represents {@link Knobs} and a {@link Configuration} created from them. */
public interface KnobContext<K, C> {
  /** Returns a collection of {@link Knobs}. */
  K knobs();

  /** Returns the current {@link Configuration}. */
  C configuration();
}
