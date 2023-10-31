package flora.experiments.sunflow;

import flora.Knob;
import flora.knob.EnumKnob;
import flora.knob.IntRangeKnob;
import java.util.concurrent.ThreadLocalRandom;

public final class RenderingKnobs {
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

  // TODO: these knobs are non-mutable so exposure without a getter is ok.
  public final IntRangeKnob threads;
  public final IntRangeKnob resolutionX;
  public final IntRangeKnob resolutionY;
  public final IntRangeKnob aaMin;
  public final IntRangeKnob aaMax;
  public final IntRangeKnob bucketSize;
  public final IntRangeKnob aoSamples;
  public final EnumKnob<Filter> filter;

  public RenderingKnobs(
      IntRangeKnob threads,
      IntRangeKnob resolutionX,
      IntRangeKnob resolutionY,
      IntRangeKnob aaMin,
      IntRangeKnob aaMax,
      IntRangeKnob bucketSize,
      IntRangeKnob aoSamples,
      EnumKnob<Filter> filter) {
    if (aaMin.start() > aaMax.start() || aaMin.end() > aaMax.end()) {
      throw new IllegalArgumentException(
          String.format(
              "The anti-aliasing knobs (aaMin=%s, aaMax=%s) do not have properly ranges.",
              aaMin, aaMax));
    }
    this.threads = threads;
    this.resolutionX = resolutionX;
    this.resolutionY = resolutionY;
    this.aaMin = aaMin;
    this.aaMax = aaMax;
    this.bucketSize = bucketSize;
    this.aoSamples = aoSamples;
    this.filter = filter;
  }

  /** The default knobs used for rendering. */
  public static final RenderingKnobs DEFAULT =
      new RenderingKnobs(
          /* threads= */ new IntRangeKnob(1, CPU_COUNT),
          /* resolutionX= */ new IntRangeKnob(128, 640),
          /* resolutionY= */ new IntRangeKnob(128, 640),
          /* aaMin= */ new IntRangeKnob(-4, 4),
          /* aaMax= */ new IntRangeKnob(-2, 4),
          /* bucketSize= */ new IntRangeKnob(16, 512),
          /* aoSamples= */ new IntRangeKnob(1, 64),
          new EnumKnob<>(Filter.class));

  public int[] randomConfiguration() {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    int aaMin = random.nextInt(this.aaMin.configurationCount());
    int aaMax = random.nextInt(this.aaMax.configurationCount());
    while (this.aaMin.fromIndex(aaMin) > this.aaMax.fromIndex(aaMax)) {
      aaMin = random.nextInt(this.aaMin.configurationCount());
      aaMax = random.nextInt(this.aaMax.configurationCount());
    }
    return new int[] {
      random.nextInt(this.threads.configurationCount()),
      // TODO: what about non-square images?
      random.nextInt(this.resolutionX.configurationCount()),
      aaMin,
      aaMax,
      random.nextInt(this.bucketSize.configurationCount()),
      random.nextInt(this.aoSamples.configurationCount()),
      random.nextInt(this.filter.configurationCount())
    };
  }

  public RenderingConfiguration fromConfiguration(int[] configuration) {
    return new RenderingConfiguration(
        threads.fromIndex(configuration[0]),
        resolutionX.fromIndex(configuration[1]),
        resolutionY.fromIndex(configuration[1]),
        aaMin.fromIndex(configuration[2]),
        aaMax.fromIndex(configuration[3]),
        bucketSize.fromIndex(configuration[4]),
        aoSamples.fromIndex(configuration[5]),
        filter.fromIndex(configuration[6]));
  }

  public Knob[] asArray() {
    return new Knob[] {threads, resolutionX, aaMin, aaMax, bucketSize, aoSamples, filter};
  }
}
