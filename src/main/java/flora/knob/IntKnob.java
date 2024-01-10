package flora.knob;

import flora.Knob;

public interface IntKnob extends Knob {
  /** Returns an integer value associated with the index. */
  int fromIndex(int index);
}
