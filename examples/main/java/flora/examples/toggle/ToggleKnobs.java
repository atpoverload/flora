package flora.examples.toggle;

import flora.knob.BooleanKnob;

/** Knobs for the {@link ToggleContext}. */
public record ToggleKnobs(BooleanKnob toggle1, BooleanKnob toggle2) {
  /** A static instance, since all boolean knobs are identical. */
  public static final ToggleKnobs INSTANCE =
      new ToggleKnobs(BooleanKnob.INSTANCE, BooleanKnob.INSTANCE);
}
