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
          /* threads= */ RenderingKnobs.DEFAULT.threads().start(),
          /* resolutionX= */ RenderingKnobs.DEFAULT.resolutionX().end(),
          /* resolutionY= */ RenderingKnobs.DEFAULT.resolutionY().end(),
          /* aaMin= */ RenderingKnobs.DEFAULT.aaMin().end(),
          /* aaMax= */ RenderingKnobs.DEFAULT.aaMax().end(),
          /* bucketSize= */ RenderingKnobs.DEFAULT.bucketSize().start(),
          /* aoSamples= */ RenderingKnobs.DEFAULT.aoSamples().start(),
          Filter.BLACKMAN_HARRIS);
}
