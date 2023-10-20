package flora.examples.toggle;

import flora.knob.BooleanKnob;

/** Knobs for the {@link Toggle}. */
public record ToggleKnobs(BooleanKnob toggle1, BooleanKnob toggle2) {
  /** A static instance, since all boolean knobs are identical. */
  public static final ToggleKnobs INSTANCE =
      new ToggleKnobs(BooleanKnob.INSTANCE, BooleanKnob.INSTANCE);

  public BooleanKnob[] asArray() {
    return new BooleanKnob[] {BooleanKnob.INSTANCE, BooleanKnob.INSTANCE};
  }
}
