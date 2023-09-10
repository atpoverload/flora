package flora.strategy;

import flora.Strategy;
import flora.context.RandomizableContext;

/** A {@link Strategy} that randomly selects a configuration and archives all updates. */
public final class RandomArchivingStrategy<K, C, KC extends RandomizableContext<K, C, KC>>
    extends ArchivingStrategy<K, C, KC> {
  private final KC context;

  public RandomArchivingStrategy(KC context) {
    this.context = context;
  }

  /** Creates knobs with random values. */
  @Override
  public final KC context() {
    return context.randomize();
  }
}
