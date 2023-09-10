package flora.experiments.sunflow;

import org.sunflow.SunflowAPI;

/** An abstract class that can configure rendering settings for a scene. */
public abstract class ConfigurableScene extends SunflowAPI {
  private final RenderingConfiguration configuration;

  protected ConfigurableScene(RenderingConfiguration configuration) {
    this.configuration = configuration;
  }

  /** Builds the configurable portion of the scene. */
  @Override
  public final void build() {
    parameter("threads", configuration.threads());
    // spawn regular priority threads
    parameter("threads.lowPriority", false);
    parameter("resolutionX", configuration.resolutionX());
    parameter("resolutionY", configuration.resolutionY());
    parameter("aa.min", configuration.aaMin());
    parameter("aa.max", configuration.aaMax());
    parameter("filter", configuration.filter().toString());
    parameter("depths.diffuse", 2);
    parameter("depths.reflection", 2);
    parameter("depths.refraction", 2);
    parameter("bucket.order", "hilbert");
    parameter("bucket.size", configuration.bucketSize());

    parameter("gi.engine", "ambocc");
    parameter("gi.ambocc.samples", configuration.aoSamples());
    parameter("gi.ambocc.maxdist", 600.0f);

    buildScene();
  }

  /** Builds the scene that is to be rendered. */
  protected abstract void buildScene();
}
