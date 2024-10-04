package flora.experiments.sunflow.renderers;

import static java.util.concurrent.Executors.newScheduledThreadPool;

import eflect.Eflect;
import eflect.util.Powercap;
import eflect.util.Rapl;
import flora.Meter;
import flora.contrib.eflect.EflectMeter;
import flora.experiments.sunflow.image.BufferedImageDisplay;
import flora.experiments.sunflow.image.ConstrainedImageDistanceMeter;
import flora.experiments.sunflow.image.ImageDistanceMeter;
import flora.experiments.sunflow.image.ImageDistanceScore;
import flora.experiments.sunflow.image.ImagePiqeMeter;
import flora.experiments.sunflow.scene.RenderingConfiguration;
import flora.experiments.sunflow.scene.RenderingKnobs;
import flora.experiments.sunflow.scene.Scene;
import flora.experiments.sunflow.scenes.CornellBox2;
import flora.experiments.sunflow.scenes.FileScene;
import flora.meter.Stopwatch;
import flora.util.LoggerUtil;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import org.sunflow.PluginRegistry;

/** Experiment code to render a given scene. */
final class RenderingEngine {
  private static final Logger logger = LoggerUtil.getLogger();

  private static final int DEFAULT_TIMEOUT =
      10 * 60 * 1000; // 5 minute timeout for the reference image (just in case)

  private final RenderingKnobs knobs;
  private final Optional<RenderingConfiguration> referenceConfiguration;
  private final Optional<String> sceneFile;
  private final BufferedImageDisplay display = new BufferedImageDisplay();
  private final OptionalDouble constraint;

  private int timeOutMs = DEFAULT_TIMEOUT;

  RenderingEngine(
      Optional<String> sceneFile,
      RenderingKnobs knobs,
      Optional<RenderingConfiguration> referenceConfiguration,
      OptionalDouble constraint) {
    this.knobs = knobs;
    this.referenceConfiguration = referenceConfiguration;
    this.sceneFile = sceneFile;
    this.constraint = constraint;
    // TODO: we know this is a discovered fault in sunflow's backend, so we add graceful termination
    PluginRegistry.imageSamplerPlugins.registerPlugin("bucket", CancelingBucketRenderer.class);
  }

  RenderingKnobs knobs() {
    return knobs;
  }

  Map<String, Meter> createMeters(ImageDistanceScore score) {
    Map<String, Meter> meters = new HashMap<>();
    meters.put("runtime", new Stopwatch());
    logger.info("checking for rapl");
    if (Rapl.getInstance() != null) {
      logger.info("found rapl!");
      meters.put("energy", new EflectMeter(Eflect.raplEflect(32, createExecutor(), 100)));
    } else if (Powercap.SOCKET_COUNT > 0) {
      logger.info("found powercap!");
      meters.put("energy", new EflectMeter(Eflect.powercapEflect(32, createExecutor(), 100)));
    } else {
      logger.info("unable to find rapl; no energy will be provided");
    }
    logger.info("adding PIQE meter");
    meters.put("piqe", new ImagePiqeMeter(display, 100.0));
    logger.info("checking for reference configuration");
    referenceConfiguration.ifPresentOrElse(
        configuration -> {
          logger.info(String.format("generating reference from %s", configuration));
          Scene scene = newScene(configuration);
          Instant start = Instant.now();
          scene.run();
          timeOutMs = (int) (3 * Duration.between(start, Instant.now()).toMillis() / 2);
          logger.info(String.format("time out is set to %d ms", timeOutMs));
          if (constraint.isPresent()) {
            LoggerUtil.getLogger()
                .info(
                    String.format(
                        "setting image quality constraint to %f", constraint.getAsDouble()));
          } else {
            logger.info("setting no image quality constraint");
          }
          meters.put(
              score.name(),
              constraint.isPresent()
                  ? new ConstrainedImageDistanceMeter(
                      display, display.getImage(), score, constraint.getAsDouble())
                  : new ImageDistanceMeter(display, display.getImage(), score));
        },
        () -> logger.info("no reference configuration to generate"));
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
