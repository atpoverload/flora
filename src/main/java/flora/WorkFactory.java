package flora;

/** An interface that produces a {@link WorkUnit}. */
public interface WorkFactory<K, C, W extends WorkUnit<K, C>> {
  /** Something made of {@link Knob(s)} that are used by the {@link WorkUnit}. */
  K knobs();

  /** The number of knobs. */
  int knobCount();

  /** The number of configurations each knob has. */
  int[] configurationSize();

  /** Creates a new work unit from the given configuration. */
  W newWorkUnit(int[] configuration);

  boolean isValid(int[] configuration);

  int[] fixConfiguration(int[] configuration);

  int[] randomConfiguration();
}
