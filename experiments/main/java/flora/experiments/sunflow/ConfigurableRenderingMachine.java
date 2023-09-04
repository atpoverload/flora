package flora.experiments.sunflow;

import flora.Knob;
import flora.Machine;
import flora.Meter;
import flora.Strategy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.sunflow.SunflowAPI;
import org.sunflow.core.Display;

/** A {@link Machine} that renders an image from the knobs in {@link ConfigurableScene}. */
public final class ConfigurableRenderingMachine extends Machine {
  /** Helper to automatically create a reference image meter using the default knobs. */
  public static ConfigurableRenderingMachine withMseReference(
      Map<String, Meter> meters,
      Strategy strategy,
      Function<Map<String, Knob>, ConfigurableScene> sceneFactory) {
    // make a reference image
    BufferedImageDisplay display = new BufferedImageDisplay();
    ConfigurableScene scene = sceneFactory.apply(ConfigurableScene.getKnobs());
    scene.build();
    scene.render(SunflowAPI.DEFAULT_OPTIONS, display);

    // add the new meter
    meters = new HashMap<>(meters);
    meters.put("mse", new ImageMseMeter(display, display.getImage()));
    return new ConfigurableRenderingMachine(meters, strategy, sceneFactory, display);
  }

  private final Strategy strategy;
  private final Function<Map<String, Knob>, ConfigurableScene> sceneFactory;
  private final Display display;

  public ConfigurableRenderingMachine(
      Map<String, Meter> meters,
      Strategy strategy,
      Function<Map<String, Knob>, ConfigurableScene> sceneFactory,
      Display display) {
    super(meters, strategy);
    this.strategy = strategy;
    this.sceneFactory = sceneFactory;
    this.display = display;
  }

  @Override
  protected final void runWorkload(Map<String, Knob> knobs) {
    ConfigurableScene scene = sceneFactory.apply(knobs);
    scene.build();
    scene.render(SunflowAPI.DEFAULT_OPTIONS, this.display);
  }

  @Override
  public String toString() {
    return strategy.toString();
  }
}
