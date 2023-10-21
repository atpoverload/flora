package flora.experiments.sunflow;

import flora.WorkUnit;
import org.sunflow.SunflowAPI;
import org.sunflow.core.Display;

/** An abstract class that can configure rendering settings for a scene. */
public abstract class ConfigurableScene extends SunflowAPI
    implements WorkUnit<RenderingKnobs, RenderingConfiguration> {
  private final RenderingKnobs knobs;
  private final RenderingConfiguration configuration;
  private final Display display;

  protected ConfigurableScene(
      RenderingKnobs knobs, RenderingConfiguration configuration, Display display) {
    this.knobs = knobs;
    this.configuration = configuration;
    this.display = display;
  }

  @Override
  public final RenderingKnobs knobs() {
    return knobs;
  }

  @Override
  public final RenderingConfiguration configuration() {
    return configuration;
  }

  @Override
  public final void run() {
    this.build();
    System.out.println(configuration);
    this.render(SunflowAPI.DEFAULT_OPTIONS, display);
  }

  public abstract ConfigurableScene newScene(
      RenderingKnobs knobs, RenderingConfiguration configuration);

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
