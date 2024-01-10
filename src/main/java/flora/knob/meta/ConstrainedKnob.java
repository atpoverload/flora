package flora.knob.meta;

import flora.Knob;

public interface ConstrainedKnob extends Knob {
  boolean isValid(int index);

  int constrain(int index);
}
