package flora.experiments.sunflow.scenes;

import flora.experiments.sunflow.ConfigurableScene;
import flora.experiments.sunflow.RenderingConfiguration;
import flora.experiments.sunflow.RenderingKnobs;
import org.sunflow.SunflowAPI;
import org.sunflow.core.Display;

/** Scene that is loaded from a given .sc file. */
public final class ConfigurableFileScene extends ConfigurableScene {
  private final Display display;
  private final int timeOutMs;
  private final String filePath;

  public ConfigurableFileScene(
      RenderingKnobs knobs,
      RenderingConfiguration configuration,
      Display display,
      int timeOutMs,
      String filePath) {
    super(knobs, configuration, display, timeOutMs);
    this.display = display;
    this.timeOutMs = timeOutMs;
    this.filePath = filePath;
  }

  @Override
  public ConfigurableScene newScene(RenderingKnobs knobs, RenderingConfiguration configuration) {
    return new ConfigurableFileScene(knobs, configuration, display, timeOutMs, filePath);
  }

  @Override
  protected void buildScene() {
    options(SunflowAPI.DEFAULT_OPTIONS);
    include(filePath);
  }
}
