package flora.strategy.mab;

/** An interface for a policy that tries to determine if we should explore configurations. */
public interface ExplorationPolicy {
  /** Returns whether we should explore or exploit. */
  <K, C, MAB extends MultiArmedBandit<K, C, MAB>> boolean doExplore(MAB bandit);

  /** Returns a knob configuration to explore. */
  <K, C, MAB extends MultiArmedBandit<K, C, MAB>> MAB explore(MAB bandit);
}
