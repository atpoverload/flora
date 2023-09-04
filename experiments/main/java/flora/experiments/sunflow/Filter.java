package flora.experiments.sunflow;

import flora.Knob;

/** Enum for the built-in filter options for {@link SunflowAPI} */
public enum Filter {
  BOX,
  TRIANGLE,
  CATMULL_ROM,
  MITCHELL,
  LANCZOS,
  BLACKMAN_HARRIS,
  SINC,
  GAUSSIAN;

  /** Turns the enum into the name sunflow expects. */
  @Override
  public String toString() {
    switch (this) {
      case BOX:
        return "box";
      case TRIANGLE:
        return "triangle";
      case CATMULL_ROM:
        return "catmull-rom";
      case MITCHELL:
        return "mitchell";
      case LANCZOS:
        return "lanczos";
      case BLACKMAN_HARRIS:
        return "blackman-harris";
      case SINC:
        return "sinc";
      case GAUSSIAN:
        return "gaussian";
    }
    throw new IllegalStateException(String.format("%s wasn't an enum?", this));
  }

  public static Filter fromKnob(Knob knob) {
    return Enum.valueOf(Filter.class, knob.getEnum(Filter.class));
  }
}
