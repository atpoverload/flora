package flora.knob;

import flora.Knob;

public final class KnobValueException extends RuntimeException {
  public KnobValueException(Knob knob, Class<?> cls, int index) {
    super(
        String.format(
            "%s has no value for %s for index %d",
            knob.getClass().getSimpleName(), cls.getSimpleName(), index));
  }
}
