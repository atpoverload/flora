package flora.experiments.sunflow.scene;

import flora.WorkUnit;
import flora.fault.MemoryFault;
import flora.fault.TimeOutFault;
import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import org.sunflow.SunflowAPI;
import org.sunflow.core.Display;
import org.sunflow.system.UI;

/** An abstract class that can configure rendering settings for a scene. */
public abstract class Scene extends SunflowAPI
    implements WorkUnit<RenderingKnobs, RenderingConfiguration> {
  private final RenderingKnobs knobs;
  private final RenderingConfiguration configuration;
  private final Display display;
  private final int timeOutMs;

  protected Scene(
      RenderingKnobs knobs, RenderingConfiguration configuration, Display display, int timeOutMs) {
    this.knobs = knobs;
    this.configuration = configuration;
    this.display = display;
    this.timeOutMs = timeOutMs;
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
    if (configuration.aaMin() > configuration.aaMax()) {
      throw new RuntimeException(
          String.format(
              "Minimum anti-aliasing (%d) exceeds the maximum (%d).",
              configuration.aaMin(), configuration.aaMax()));
    }
    this.build();
    Instant start = Instant.now();
    var timer = new Timer();
    AtomicBoolean timedOut = new AtomicBoolean(false);
    timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            timedOut.set(true);
            UI.taskCancel();
          }
        },
        timeOutMs);
    try {
      this.render(SunflowAPI.DEFAULT_OPTIONS, display);
    } catch (OutOfMemoryError e) {
      timer.cancel();
      throw new MemoryFault(MemoryFault.MemoryFaultType.OUT_OF_MEMORY);
    }
    timer.cancel();
    Duration elapsed = Duration.between(start, Instant.now());
    if (timedOut.get()) {
      throw new TimeOutFault(elapsed, Duration.ofMillis(timeOutMs));
    }
  }

  /** Builds the configurable portion of the scene. */
  @Override
  public final void build() {
    buildScene();
    buildConfigurablePortion();
  }

  private void buildConfigurablePortion() {
    parameter("threads", configuration.threads());
    // spawn regular priority threads
    parameter("threads.lowPriority", false);
    parameter("resolutionX", configuration.width());
    parameter("resolutionY", configuration.height());
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

    options(SunflowAPI.DEFAULT_OPTIONS);
  }

  /** Builds the scene that is to be rendered. */
  protected abstract void buildScene();
}
