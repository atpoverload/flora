package flora.experiments.rendering;

import flora.WorkFactory;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public final class RenderingWorkFactory
    implements WorkFactory<RenderingKnobs, RenderingConfiguration, RenderingWorkUnit> {
  private final RenderingKnobs knobs;
  private final BlockingQueue<RenderingConfiguration> nextConfiguration;
  private final int[] configurationSize;

  public RenderingWorkFactory(
      RenderingKnobs knobs, BlockingQueue<RenderingConfiguration> nextConfiguration) {
    this.knobs = knobs;
    this.nextConfiguration = nextConfiguration;
    this.configurationSize =
        new int[] {
          KnobUtils.getConfigurationCount(knobs.getResolutionX()),
          KnobUtils.getConfigurationCount(knobs.getResolutionY()),
        };
  }

  @Override
  public RenderingKnobs knobs() {
    return this.knobs;
  }

  /** The number of knobs. */
  @Override
  public int knobCount() {
    return 2;
  }

  /** The number of configurations each knob has. */
  @Override
  public int[] configurationSize() {
    return Arrays.copyOf(configurationSize, configurationSize.length);
  }

  @Override
  public int[] decode(RenderingConfiguration configuration) {
    return new int[0];
  }

  /** Creates a new work unit from the given configuration. */
  @Override
  public RenderingWorkUnit newWorkUnit(int[] configuration) {
    return new RenderingWorkUnit(
        knobs,
        RenderingConfiguration.newBuilder()
            .setResolutionX(KnobUtils.getRangeValue(configuration[0], knobs.getResolutionX()))
            .setResolutionY(KnobUtils.getRangeValue(configuration[1], knobs.getResolutionY()))
            .build(),
        nextConfiguration);
  }

  @Override
  public boolean isValid(int[] configuration) {
    return true;
  }

  @Override
  public int[] fixConfiguration(int[] configuration) {
    return configuration;
  }

  @Override
  public int[] randomConfiguration() {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    return new int[] {
      random.nextInt(configurationSize[0] + 1), random.nextInt(configurationSize[1] + 1),
    };
  }
}
