package flora.experiments.sunflow.renderers;

import static java.util.concurrent.Executors.newScheduledThreadPool;

import eflect.Eflect;
import eflect.util.Powercap;
import eflect.util.Rapl;
import flora.Meter;
import flora.contrib.eflect.EflectMeter;
import flora.experiments.sunflow.image.BufferedImageDisplay;
import flora.experiments.sunflow.image.ImageDistanceMeter;
import flora.experiments.sunflow.image.ImageDistanceScore;
import flora.experiments.sunflow.scene.RenderingConfiguration;
import flora.experiments.sunflow.scene.RenderingKnobs;
import flora.experiments.sunflow.scene.Scene;
import flora.experiments.sunflow.scenes.CornellBox2;
import flora.experiments.sunflow.scenes.FileScene;
import flora.meter.Stopwatch;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import org.sunflow.PluginRegistry;

/** Experiment code to render a given scene. */
final class RenderingEngine {
  private static final int DEFAULT_TIMEOUT =
      60 * 60 * 1000; // 1 hour timeout for the reference image (just in case)

  private final RenderingKnobs knobs;
  private final Optional<RenderingConfiguration> referenceConfiguration;
  private final Optional<String> sceneFile;
  private final BufferedImageDisplay display = new BufferedImageDisplay();

  private int timeOutMs = DEFAULT_TIMEOUT;

  RenderingEngine(
      Optional<String> sceneFile,
      RenderingKnobs knobs,
      Optional<RenderingConfiguration> referenceConfiguration) {
    this.knobs = knobs;
    this.referenceConfiguration = referenceConfiguration;
    this.sceneFile = sceneFile;
    // TODO: we know this is a discovered fault in sunflow's backend, so we add graceful termination
    PluginRegistry.imageSamplerPlugins.registerPlugin("bucket", CancelingBucketRenderer.class);
  }

  RenderingKnobs knobs() {
    return knobs;
  }

  Map<String, Meter> createMeters(ImageDistanceScore score) {
    Map<String, Meter> meters = new HashMap<>();
    meters.put("runtime", new Stopwatch());
    if (Rapl.getInstance() != null) {
      meters.put("energy", new EflectMeter(Eflect.raplEflect(4, createExecutor(), 100)));
    } else if (Powercap.SOCKET_COUNT > 0) {
      meters.put("energy", new EflectMeter(Eflect.powercapEflect(4, createExecutor(), 100)));
    }
    referenceConfiguration.ifPresent(
        configuration -> {
          Scene scene = newScene(configuration);
          Instant start = Instant.now();
          scene.run();
          timeOutMs = (int) (3 * Duration.between(start, Instant.now()).toMillis() / 2);
          meters.put("score", new ImageDistanceMeter(display, display.getImage(), score));
        });
    return meters;
  }

  Scene newScene(RenderingConfiguration configuration) {
    if (sceneFile.isEmpty()) {
      return new CornellBox2(knobs, configuration, display, timeOutMs);
    } else {
      return new FileScene(sceneFile.get(), knobs, configuration, display, timeOutMs);
    }
  }

  private static ScheduledExecutorService createExecutor() {
    final AtomicInteger counter = new AtomicInteger(0);
    return newScheduledThreadPool(
        3,
        r -> {
          Thread t = new Thread(r, "eflect-" + counter.getAndIncrement());
          t.setDaemon(true);
          return t;
        });
  }
}
