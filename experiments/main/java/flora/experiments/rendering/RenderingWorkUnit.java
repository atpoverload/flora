package flora.experiments.rendering;

import flora.WorkUnit;
import java.util.concurrent.BlockingQueue;

public final class RenderingWorkUnit
    implements WorkUnit<RenderingKnobs, RenderingConfiguration> {
  private final RenderingKnobs knobs;
  private final RenderingConfiguration configuration;
  private final BlockingQueue<RenderingConfiguration> nextConfiguration;

  RenderingWorkUnit(
      RenderingKnobs knobs,
      RenderingConfiguration configuration,
      BlockingQueue<RenderingConfiguration> nextConfiguration) {
    this.knobs = knobs;
    this.configuration = configuration;
    this.nextConfiguration = nextConfiguration;
  }

  @Override
  public RenderingKnobs knobs() {
    return knobs;
  }

  @Override
  public RenderingConfiguration configuration() {
    return configuration;
  }

  @Override
  public void run() {
    nextConfiguration.add(configuration);
  }
}
