package flora.experiments.sunflow.scene;

import flora.WorkFactory;
import flora.knob.util.Knobs;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public final class SquareSceneFactory
    implements WorkFactory<RenderingKnobs, RenderingConfiguration, Scene> {
  private final RenderingKnobs knobs;
  private final Function<RenderingConfiguration, Scene> sceneFactory;

  public SquareSceneFactory(
      RenderingKnobs knobs, Function<RenderingConfiguration, Scene> sceneFactory) {
    this.knobs = knobs;
    this.sceneFactory = sceneFactory;
  }

  public RenderingKnobs knobs() {
    return knobs;
  }

  public int knobCount() {
    return 7;
  }

  public int[] configurationSize() {
    return new int[] {
      knobs.threads.configurationCount(),
      knobs.width.configurationCount(),
      knobs.aaMin.configurationCount(),
      knobs.aaMax.configurationCount(),
      knobs.bucketSize.configurationCount(),
      knobs.aoSamples.configurationCount(),
      knobs.filter.configurationCount(),
    };
  }

  public Scene newWorkUnit(int[] configuration) {

    return sceneFactory.apply(
        new RenderingConfiguration(
            knobs.threads.fromIndex(configuration[0]),
            knobs.width.fromIndex(configuration[1]),
            knobs.height.fromIndex(configuration[1]),
            knobs.aaMin.fromIndex(configuration[2]),
            knobs.aaMax.fromIndex(configuration[3]),
            knobs.bucketSize.fromIndex(configuration[4]),
            knobs.aoSamples.fromIndex(configuration[5]),
            knobs.filter.fromIndex(configuration[6])));
  }

  public boolean isValid(int[] configuration) {
    if (configuration[0] < 0 || knobs.threads.configurationCount() < configuration[0]) {
      return false;
    }
    if (configuration[1] < 0 || knobs.width.configurationCount() < configuration[1]) {
      return false;
    }
    if (configuration[2] < 0 || knobs.aaMin.configurationCount() < configuration[2]) {
      return false;
    }
    if (configuration[3] < 0 || knobs.aaMax.configurationCount() < configuration[3]) {
      return false;
    }
    if (configuration[4] < 0 || knobs.bucketSize.configurationCount() < configuration[4]) {
      return false;
    }
    if (configuration[5] < 0 || knobs.aoSamples.configurationCount() < configuration[5]) {
      return false;
    }
    if (configuration[6] < 0 || knobs.filter.configurationCount() < configuration[6]) {
      return false;
    }
    return knobs.aaMin.fromIndex(configuration[2]) <= knobs.aaMax.fromIndex(configuration[3]);
  }

  public int[] fixConfiguration(int[] configuration) {
    int[] fixedConfiguration = new int[knobCount()];
    fixedConfiguration[0] =
        Math.min(Math.max(0, configuration[0]), knobs.threads.configurationCount() - 1);
    fixedConfiguration[1] =
        Math.min(Math.max(0, configuration[1]), knobs.width.configurationCount() - 1);
    fixedConfiguration[2] =
        Math.min(Math.max(0, configuration[2]), knobs.aaMin.configurationCount() - 1);
    fixedConfiguration[3] =
        Math.min(Math.max(0, configuration[3]), knobs.aaMax.configurationCount() - 1);
    fixedConfiguration[4] =
        Math.min(Math.max(0, configuration[4]), knobs.bucketSize.configurationCount() - 1);
    fixedConfiguration[5] =
        Math.min(Math.max(0, configuration[5]), knobs.aoSamples.configurationCount() - 1);
    fixedConfiguration[6] =
        Math.min(Math.max(0, configuration[6]), knobs.filter.configurationCount() - 1);

    int aaMin = fixedConfiguration[2];
    int aaMax = fixedConfiguration[3];
    while (knobs.aaMax.fromIndex(aaMax) < knobs.aaMin.fromIndex(aaMin)) {
      aaMax++;
    }
    fixedConfiguration[2] = aaMin;
    fixedConfiguration[3] = aaMax;

    return fixedConfiguration;
  }

  public int[] randomConfiguration() {
    ThreadLocalRandom random = ThreadLocalRandom.current();

    int[] configuration = new int[7];
    configuration[0] = random.nextInt(0, knobs.threads.configurationCount());
    configuration[1] = random.nextInt(0, knobs.width.configurationCount());
    configuration[4] = random.nextInt(0, knobs.bucketSize.configurationCount());
    configuration[5] = random.nextInt(0, knobs.aoSamples.configurationCount());
    configuration[6] = random.nextInt(0, knobs.filter.configurationCount());

    int aaMin = random.nextInt(0, knobs.aaMin.configurationCount());
    int aaMax = random.nextInt(0, knobs.aaMax.configurationCount());
    while (knobs.aaMax.fromIndex(aaMax) < knobs.aaMin.fromIndex(aaMin)) {
      aaMax++;
    }
    configuration[2] = aaMin;
    configuration[3] = aaMax;

    return configuration;
  }

  public int[] decode(RenderingConfiguration configuration) {
    return new int[] {
      knobs.threads.toIndex(Math.min(Knobs.ALL_CPUS.end(), configuration.threads())),
      knobs.width.toIndex(configuration.width()),
      knobs.aaMin.toIndex(configuration.aaMin()),
      knobs.aaMax.toIndex(configuration.aaMax()),
      knobs.bucketSize.toIndex(configuration.bucketSize()),
      knobs.aoSamples.toIndex(configuration.aoSamples()),
      knobs.filter.toIndex(configuration.filter())
    };
  }
}
