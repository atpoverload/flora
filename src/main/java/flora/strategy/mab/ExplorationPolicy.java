package flora.strategy.mab;

/** An interface for a policy that tries to determine if we should explore configurations. */
public interface ExplorationPolicy {
  /** Returns whether we should explore or exploit. */
  <K, C, KC extends MultiArmedBandit<K, C, KC>> boolean doExplore(KC bandit);

  /** Returns a knob configuration to explore. */
  <K, C, KC extends MultiArmedBandit<K, C, KC>> KC explore(KC bandit);
}
