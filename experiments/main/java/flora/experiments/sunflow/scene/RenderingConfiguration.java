package flora.experiments.sunflow.scene;

public record RenderingConfiguration(
    int threads,
    int width,
    int height,
    int aaMin,
    int aaMax,
    int bucketSize,
    int aoSamples,
    Filter filter) {}
