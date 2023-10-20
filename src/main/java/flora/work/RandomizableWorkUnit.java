package flora.work;

import flora.WorkUnit;

/** A {@link WorkUnit} that can produce a random copy. */
public interface RandomizableWorkUnit<K, C, W extends RandomizableWorkUnit<K, C, W>>
    extends WorkUnit<K, C> {
  /** Returns a copy of the {@link WorkUnit} with a randomly chosen configuration. */
  W random();
}
