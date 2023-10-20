package flora.knob.meta;

import flora.Knob;
import java.util.concurrent.ThreadLocalRandom;

/** A {@link Knob} that constrains to the range of another knob's configurations. */
public final class RangeConstrainedKnob implements ConstrainedKnob, RandomizableKnob {
  private final Knob knob;

  public RangeConstrainedKnob(Knob knob) {
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

  /** Returns if the index is in the underlying knob's range. */
  @Override
  public boolean isValid(int index) {
    return 0 <= index && index < configurationCount();
  }

  /** Constrains the index to the underlying knob's range. */
  @Override
  public int constrain(int index) {
    return Math.max(0, Math.min(index, configurationCount()));
  }

  /** Returns a random index of the underlying knob. */
  @Override
  public int random() {
    return ThreadLocalRandom.current().nextInt(0, configurationCount());
  }
}
