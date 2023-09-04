package green.knob;

import green.Knob;
import java.util.concurrent.atomic.AtomicBoolean;

/** A {@link Knob} that contains a boolean. */
public final class BooleanKnob implements Knob {
  private final AtomicBoolean value;

  public BooleanKnob(boolean value) {
    this.value = new AtomicBoolean(value);
  }

  /** Writes the knob as a json dict. */
  @Override
  public String toString() {
    return String.format("{\"knob_type\":\"%s\",\"value\":%b", this.getClass(), this.value.get());
  }

  /** Returns the value. */
  @Override
  public boolean getBoolean() {
    return this.value.get();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof BooleanKnob) {
      BooleanKnob other = (BooleanKnob) o;
      return this.value.get() == other.value.get();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Boolean.hashCode(this.value.get());
  }

  /** Sets the value. It is usually better to construct a new knob instead of modifying one. */
  public void setValue(boolean value) {
    this.value.set(value);
  }
}
