package flora.examples.toggle;

import flora.knob.BooleanKnob;

/** Knobs for the {@link ToggleContext}. */
public record ToggleKnobs(BooleanKnob toggle1, BooleanKnob toggle2) {
  private static final ToggleKnobs instance =
      new ToggleKnobs(BooleanKnob.instance(), BooleanKnob.instance());

  /** Returns a static instance, since all boolean knobs are identical. */
  public static ToggleKnobs instance() {
    return instance;
  }
}
