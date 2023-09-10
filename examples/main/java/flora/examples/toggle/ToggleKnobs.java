package flora.examples.toggle;

import flora.knob.BooleanKnob;

/** Knobs for the {@link ToggleMachine}. */
record ToggleKnobs(BooleanKnob toggle1, BooleanKnob toggle2) {
  private static final ToggleKnobs instance =
      new ToggleKnobs(BooleanKnob.instance(), BooleanKnob.instance());

  static ToggleKnobs instance() {
    return instance;
  }
}
