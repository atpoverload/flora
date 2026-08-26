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
  private final Runnable barrier;

  public RenderingWorkFactory(
      RenderingKnobs knobs,
      BlockingQueue<RenderingConfiguration> nextConfiguration,
      Runnable barrier) {
    this.knobs = knobs;
    this.nextConfiguration = nextConfiguration;
    this.configurationSize =
        new int[] {
          KnobUtils.getConfigurationCount(knobs.getResolutionX()),
          // Sticking with square images
          // KnobUtils.getConfigurationCount(knobs.getResolutionY()),
          KnobUtils.getConfigurationCount(knobs.getAaSamples()),
          KnobUtils.getConfigurationCount(knobs.getAoSamples()),
          KnobUtils.getConfigurationCount(knobs.getThreads()),
          knobs.getFilterCount()
        };
    this.barrier = barrier;
  }

  @Override
  public RenderingKnobs knobs() {
    return this.knobs;
  }

  /** The number of knobs. */
  @Override
  public int knobCount() {
    return configurationSize.length;
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
            // Sticking with square images for now
            .setResolutionY(KnobUtils.getRangeValue(configuration[0], knobs.getResolutionY()))
            .setAaSamples(KnobUtils.getRangeValue(configuration[1], knobs.getAaSamples()))
            .setAoSamples(KnobUtils.getRangeValue(configuration[2], knobs.getAoSamples()))
            .setThreads(KnobUtils.getRangeValue(configuration[3], knobs.getThreads()))
            .setFilter(knobs.getFilterList().get(configuration[4]))
            .build(),
        nextConfiguration,
        barrier);
  }

  @Override
  public boolean isValid(int[] configuration) {
    System.out.println(Arrays.toString(configuration));
    return true;
  }

  @Override
  public int[] fixConfiguration(int[] configuration) {
    System.out.println(Arrays.toString(configuration));
    return configuration;
  }

  @Override
  public int[] randomConfiguration() {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    return new int[] {
      random.nextInt(configurationSize[0]),
      random.nextInt(configurationSize[1]),
      random.nextInt(configurationSize[2]),
      random.nextInt(configurationSize[3]),
      random.nextInt(configurationSize[4])
    };
  }
}
