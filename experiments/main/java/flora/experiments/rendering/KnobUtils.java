package flora.experiments.rendering;

import flora.knob.RangeKnob;

public final class KnobUtils {
  public static int getConfigurationCount(RangeKnob knob) {
    return (knob.getEnd() - knob.getStart()) / knob.getStep();
  }

  public static int getRangeValue(int index, RangeKnob knob) {
    return index * knob.getStep() + knob.getStart();
  }

  private KnobUtils() {} // Do not instantiate.
}
