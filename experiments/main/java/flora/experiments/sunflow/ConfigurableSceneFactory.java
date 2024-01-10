package flora.experiments.sunflow;

import flora.knob.meta.RangeConstrainedKnob;
import flora.work.EncodedWorkFactory;
import java.util.Arrays;

public final class ConfigurableSceneFactory
    implements EncodedWorkFactory<RenderingKnobs, RenderingConfiguration, ConfigurableScene> {
  private final ConfigurableScene scene;
  private final RangeConstrainedKnob[] knobs;

  public ConfigurableSceneFactory(ConfigurableScene scene) {
    this.scene = scene;
    this.knobs =
        Arrays.stream(scene.knobs().asArray())
            .map(RangeConstrainedKnob::new)
            .toArray(RangeConstrainedKnob[]::new);
  }

  @Override
  public RenderingKnobs knobs() {
    return this.scene.knobs();
  }

  @Override
  public int knobCount() {
    return this.knobs.length;
  }

  @Override
  public int configurationCount(int knob) {
    return this.knobs[knob].configurationCount();
  }

  @Override
  public ConfigurableScene newWorkUnit(int[] configuration) {
    return scene.newScene(scene.knobs(), scene.knobs().fromConfiguration(configuration));
  }

  @Override
  public int[] randomConfiguration() {
    return scene.knobs().randomConfiguration();
  }

  @Override
  public boolean isValidConfiguration(int[] configuration) {
    if (configuration.length != knobs.length) {
      return false;
    }
    for (int i = 0; i < configuration.length; i++) {
      if (!this.knobs[i].isValid(configuration[i])) {
        return false;
      }
    }
    // check that anti-aliasing (knobs 3 and 4) are properly bounded
    return this.knobs[2].fromIndex(configuration[2], Integer.class)
        <= this.knobs[3].fromIndex(configuration[3], Integer.class);
  }

  @Override
  public int[] repairConfiguration(int[] configuration) {
        if (configuration.length != knobs.length) {
      // can't be repaired; select a random one
      return randomConfiguration();
    }
    for (int i = 0; i < configuration.length; i++) {
      configuration[i] = this.knobs[i].constrain(configuration[i]);
    }
    // check that anti-aliasing (knobs 3 and 4) are properly bounded
    while (this.knobs[2].fromIndex(configuration[2], Integer.class)
        > this.knobs[3].fromIndex(configuration[3], Integer.class)) {
      configuration[3]++;
    }
        return configuration;
  }
}
