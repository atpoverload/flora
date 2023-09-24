package flora.knob.meta;

import flora.Knob;
import java.util.concurrent.ThreadLocalRandom;

/** A {@link Knob} that constrains to the range of another knob's configurations. */
public final class MetaKnob implements ConstrainedKnob, RandomizableKnob {
  private final Knob knob;

  public MetaKnob(Knob knob) {
    this.knob = knob;
  }

  /** Returns the underlying knob's configuration count. */
  @Override
  public int configurationCount() {
    return this.knob.configurationCount();
  }

  /** Returns the index from the underlying knob. */
  @Override
  public <T extends Object> T fromIndex(int index, Class<T> cls) {
    return this.knob.fromIndex(index, cls);
  }

  /** Constrains the index to the underlying knob's index range. */
  @Override
  public int constrainIndex(int index) {
    return Math.max(0, Math.min(index, configurationCount()));
  }

  /** Returns a random index of the underlying knob. */
  @Override
  public int randomIndex() {
    return ThreadLocalRandom.current().nextInt(0, configurationCount());
  }
}
