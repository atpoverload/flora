package flora.knob.meta;

import flora.Knob;

public interface ConstrainedKnob extends Knob {
    int constrainIndex(int index);
}
