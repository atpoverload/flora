package flora.experiments.sunflow.scene;

import flora.knob.EnumKnob;
import flora.knob.IntRangeKnob;

public final class RenderingKnobs {
  // TODO: these knobs are non-mutable so exposure without a getter is ok.
  public final IntRangeKnob threads;
  public final IntRangeKnob width;
  public final IntRangeKnob height;
  public final IntRangeKnob aaMin;
  public final IntRangeKnob aaMax;
  public final IntRangeKnob bucketSize;
  public final IntRangeKnob aoSamples;
  public final EnumKnob<Filter> filter;

  public RenderingKnobs(
      IntRangeKnob threads,
      IntRangeKnob width,
      IntRangeKnob height,
      IntRangeKnob aaMin,
      IntRangeKnob aaMax,
      IntRangeKnob bucketSize,
      IntRangeKnob aoSamples,
      EnumKnob<Filter> filter) {
    if (aaMin.start() > aaMax.start() || aaMin.end() > aaMax.end()) {
      throw new IllegalArgumentException(
          String.format(
              "The anti-aliasing knobs (aaMin=%s, aaMax=%s) do not have proper ranges.",
              aaMin, aaMax));
    }
    this.threads = threads;
    this.width = width;
    this.height = height;
    this.aaMin = aaMin;
    this.aaMax = aaMax;
    this.bucketSize = bucketSize;
    this.aoSamples = aoSamples;
    this.filter = filter;
  }
}
