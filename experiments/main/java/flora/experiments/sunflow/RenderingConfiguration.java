package flora.experiments.sunflow;

public record RenderingConfiguration(
    int threads,
    int resolutionX,
    int resolutionY,
    int aaMin,
    int aaMax,
    int bucketSize,
    int aoSamples,
    Filter filter) {}
