package flora.knob;

import flora.Knob;

public final class KnobIndexException extends RuntimeException {
  public <T> KnobIndexException(Knob knob, T value) {
    super(String.format("%s has no index for value %s", knob.getClass().getSimpleName(), value));
  }
}
