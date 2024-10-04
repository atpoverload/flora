package flora.experiments.sunflow;

import flora.WorkFactory;
import flora.knob.meta.RangeConstrainedKnob;
import java.util.Arrays;

public final class ConfigurableSceneFactory
    implements WorkFactory<RenderingKnobs, RenderingConfiguration, ConfigurableScene> {
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
  public int[] configurationSize() {
    return Arrays.stream(this.knobs).mapToInt(k -> k.configurationCount()).toArray();
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
  public boolean isValid(int[] configuration) {
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
  public int[] fixConfiguration(int[] configuration) {
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
    while (this.knobs[4].fromIndex(configuration[4], Integer.class)
        > this.knobs[1].fromIndex(configuration[1], Integer.class)) {
      configuration[4]--;
    }
    return configuration;
  }

  public int[] decode(RenderingConfiguration configuration) {
    return new int[0];
  }
}
