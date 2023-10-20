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
          /* threads= */ 12,
          /* resolutionX= */ 768,
          /* resolutionY= */ 768,
          /* aaMin= */ 1,
          /* aaMax= */ 2,
          /* bucketSize= */ 60,
          /* aoSamples= */ 32,
          Filter.BLACKMAN_HARRIS);
}
