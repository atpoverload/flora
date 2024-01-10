package flora.strategy.archiving;

import flora.Strategy;
import flora.work.RandomizableWorkUnit;

/** A {@link Strategy} that randomly selects a configuration and archives all updates. */
public final class RandomArchivingStrategy<K, C, W extends RandomizableWorkUnit<K, C, W>>
    extends ArchivingStrategy<K, C, W> {
  private final W workload;

  public RandomArchivingStrategy(W workload) {
    this.workload = workload;
  }

  /** Creates knobs with random values. */
  @Override
  public W nextWorkload() {
    return workload.random();
  }
}
