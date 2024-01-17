package flora.experiments.sunflow.scenes;

import flora.experiments.sunflow.scene.RenderingConfiguration;
import flora.experiments.sunflow.scene.RenderingKnobs;
import flora.experiments.sunflow.scene.Scene;
import org.sunflow.SunflowAPI;
import org.sunflow.core.Display;

/** Scene that is loaded from a given .sc file. */
public final class FileScene extends Scene {
  private final String filePath;

  public FileScene(
      String filePath,
      RenderingKnobs knobs,
      RenderingConfiguration configuration,
      Display display,
      int timeOutMs) {
    super(knobs, configuration, display, timeOutMs);
    this.filePath = filePath;
  }

  @Override
  protected void buildScene() {
    options(SunflowAPI.DEFAULT_OPTIONS);
    include(filePath);
  }
}
