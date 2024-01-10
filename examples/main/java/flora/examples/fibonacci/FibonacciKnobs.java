package flora.examples.fibonacci;

import flora.knob.IntRangeKnob;

/** Knobs for the {@link FibonacciBandit}. */
final record FibonacciKnobs(IntRangeKnob threads, IntRangeKnob n) {}
