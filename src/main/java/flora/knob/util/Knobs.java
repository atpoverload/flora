package flora.knob.util;

import flora.knob.IntRangeKnob;

public class Knobs {
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

  public static final IntRangeKnob ALL_CPUS = new IntRangeKnob(1, CPU_COUNT);
}
