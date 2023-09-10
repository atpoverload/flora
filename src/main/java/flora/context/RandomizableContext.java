package flora.context;

import flora.KnobContext;

/** An interface for a context that can produce a randomized version of itself. */
public interface RandomizableContext<K, C, KC extends KnobContext<K, C>> extends KnobContext<K, C> {
  /** Returns a randomized version of this. */
  KC randomize();
}
