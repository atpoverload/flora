package flora;

/** An interface that represents {@link Knobs} and a {@link Configuration} created from them. */
public interface WorkloadContext<K, C> {
  /** Returns something made of {@link Knob(s)}. */
  K knobs();

  /** Returns the current {@link Configuration}. */
  C configuration();
}
