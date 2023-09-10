package flora.strategy.mab;

import flora.Strategy;
import java.util.Map;

/** A {@link Strategy} that implements the MAB algorithm through configuration weighting. */
public final class MultiArmedBanditStrategy<K, C, KC extends MultiArmedBandit<K, C, KC>>
    implements Strategy<K, C, KC> {
  private final KC bandit;
  private final ExplorationPolicy explorationPolicy;
  private final ExploitationPolicy exploitationPolicy;

  public MultiArmedBanditStrategy(
      KC bandit,
      ExplorationPolicy explorationPolicy,
      ExploitationPolicy exploitationPolicy) {
    this.bandit = bandit;
    this.explorationPolicy = explorationPolicy;
    this.exploitationPolicy = exploitationPolicy;
  }

  /** Check if we need to explore. Otherwise, exploit. */
  @Override
  public final KC context() {
    if (explorationPolicy.doExplore(bandit)) {
      return explorationPolicy.explore(bandit);
    } else {
      return exploitationPolicy.exploit(bandit);
    }
  }

  /** Adds to the total reward for the configuration. */
  @Override
  public final void update(KC bandit, Map<String, Double> measurement) {
    this.bandit.addConfiguration(bandit, measurement);
  }
}
