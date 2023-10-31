package flora.experiments.sunflow;

public record RenderingConfiguration(
    int threads,
    int resolutionX,
    int resolutionY,
    int aaMin,
    int aaMax,
    int bucketSize,
    int aoSamples,
    Filter filter) {
  /** The default knobs used for the reference image. */
  public static final RenderingConfiguration DEFAULT =
      new RenderingConfiguration(
          12,
          RenderingKnobs.DEFAULT.resolutionX.start(),
          RenderingKnobs.DEFAULT.resolutionY.start(),
          /* aaMin= */ 1,
          /* aaMax= */ 2,
          RenderingKnobs.DEFAULT.bucketSize.start(),
          RenderingKnobs.DEFAULT.aoSamples.start(),
          Filter.BLACKMAN_HARRIS);
}
