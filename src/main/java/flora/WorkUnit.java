package flora;

/** An interface that represents {@link Knobs} and a {@link Configuration} created from them. */
public interface WorkUnit<K, C> {
  /** Returns something made of {@link Knob(s)}. */
  K knobs();

  /** Returns the current {@link Configuration}. */
  C configuration();

  /** The unit of work. */
  void run();
}
