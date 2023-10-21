package flora.strategy.contrib.ears;

import flora.Knob;
import flora.WorkUnit;

public interface RawWorkFactory<K extends Knob, W extends WorkUnit<?, ?>> {
  /** Returns something made of {@link Knob(s)}. */
  K[] knobs();

  W fromIndices(int[] configuration);
}
