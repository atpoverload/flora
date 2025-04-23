package flora.experiments.rendering;

import flora.WorkFactory;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

public final class RenderingWorkFactory
    implements WorkFactory<RenderingKnobs, RenderingConfiguration, RenderingWorkUnit> {
  private final RenderingKnobs knobs;
  private final BlockingQueue<RenderingConfiguration> nextConfiguration;

  public RenderingWorkFactory(
      RenderingKnobs knobs, BlockingQueue<RenderingConfiguration> nextConfiguration) {
    this.knobs = knobs;
    this.nextConfiguration = nextConfiguration;
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
    return new int[] {
      knobs.getResolutionX().getEnd() - knobs.getResolutionX().getStart(),
      knobs.getResolutionY().getEnd() - knobs.getResolutionY().getStart(),
    };
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
            .setResolutionX(configuration[0])
            .setResolutionY(configuration[1])
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
    return new int[0];
  }
}
