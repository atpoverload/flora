package flora.experiments.sunflow;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import flora.Knob;
import flora.Machine;
import flora.Meter;
import flora.Strategy;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.sunflow.SunflowAPI;
import org.sunflow.core.Display;
import org.sunflow.system.UI;

/** A variation on the rendering machine that will kill the rendering if it takes too long. */
public final class KillableConfigurableRenderingMachine extends Machine {
  // The fraction of time beyond the reference that is allowed
  private static final double THRESHOLD = 1.2;

  /** Helper to automatically create a reference image meter using the default knobs. */
  public static KillableConfigurableRenderingMachine withMseReference(
      Map<String, Meter> meters,
      Strategy strategy,
      Function<Map<String, Knob>, ConfigurableScene> sceneFactory) {
    // make a reference image
    BufferedImageDisplay display = new BufferedImageDisplay();
    ConfigurableScene scene = sceneFactory.apply(ConfigurableScene.getKnobs());
    scene.build();
    Instant start = Instant.now();
    scene.render(SunflowAPI.DEFAULT_OPTIONS, display);
    Duration elapsed = Duration.between(start, Instant.now());

    // add the new meters
    meters = new HashMap<>(meters);
    meters.put("mse", new ImageMseMeter(display, display.getImage()));
    AtomicBoolean killSwitch = new AtomicBoolean();
    meters.put("killed", new KillSwitchMeter(killSwitch));
    return new KillableConfigurableRenderingMachine(
        meters,
        strategy,
        sceneFactory,
        display,
        (long) (THRESHOLD * elapsed.toMillis()),
        killSwitch);
  }

  private final Strategy strategy;
  private final Function<Map<String, Knob>, ConfigurableScene> sceneFactory;
  private final Display display;
  private final long maxDuration;
  private final AtomicBoolean killSwitch;
  private final ScheduledExecutorService killSwitchExecutor;

  public KillableConfigurableRenderingMachine(
      Map<String, Meter> meters,
      Strategy strategy,
      Function<Map<String, Knob>, ConfigurableScene> sceneFactory,
      Display display,
      long maxDuration,
      AtomicBoolean killSwitch) {
    super(meters, strategy);
    this.strategy = strategy;
    this.sceneFactory = sceneFactory;
    this.display = display;
    this.maxDuration = maxDuration;
    this.killSwitch = killSwitch;
    this.killSwitchExecutor =
        newSingleThreadScheduledExecutor(
            r -> {
              Thread t = new Thread(r, String.format("kill-switch"));
              t.setDaemon(true);
              return t;
            });
  }

  @Override
  protected final void runWorkload(Map<String, Knob> knobs) {
    killSwitch.set(false);
    ConfigurableScene scene = sceneFactory.apply(knobs);
    scene.build();
    Future<?> future =
        killSwitchExecutor.schedule(
            () -> {
              UI.taskCancel();
              killSwitch.set(true);
            },
            maxDuration,
            TimeUnit.MILLISECONDS);
    scene.render(SunflowAPI.DEFAULT_OPTIONS, this.display);
    future.cancel(true);
  }

  @Override
  public String toString() {
    return strategy.toString();
  }
}
