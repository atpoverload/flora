package flora.strategy.archiving;

import flora.Strategy;
import flora.context.RandomizableContext;

/** A {@link Strategy} that randomly selects a configuration and archives all updates. */
public final class RandomArchivingStrategy<K, C, Ctx extends RandomizableContext<K, C, Ctx>>
    extends ArchivingStrategy<K, C, Ctx> {
  private final Ctx context;

  public RandomArchivingStrategy(Ctx context) {
    this.context = context;
  }

  /** Creates knobs with random values. */
  @Override
  public final Ctx context() {
    return context.random();
  }
}
