package flora.strategy.contrib.ears;

import flora.Knob;
import flora.knob.meta.ConstrainedKnob;
import flora.knob.meta.RandomizableKnob;
import java.util.concurrent.ThreadLocalRandom;

public final class EarsKnob implements ConstrainedKnob, RandomizableKnob {
  private final Knob knob;

  public EarsKnob(Knob knob) {
    this.knob = knob;
  }

  /** Returns the underlying knob's configuration count. */
  @Override
  public int configurationCount() {
    return this.knob.configurationCount();
  }

  /** Returns the value for underlying knob's {@code index}. */
  @Override
  @SuppressWarnings("unchecked")
  public <T extends Object> T fromIndex(int index, Class<T> cls) {
    return this.knob.fromIndex(index, cls);
  }

  /** Returns a random, valid index for the underlying knob. */
  @Override
  public int randomIndex() {
    return ThreadLocalRandom.current().nextInt(0, this.knob.configurationCount());
  }

  /** Restricts the index to the underlying knob's range. */
  @Override
  public int constrainIndex(int index) {
    return Math.max(0, Math.min(index, knob.configurationCount() - 1));
  }
}
