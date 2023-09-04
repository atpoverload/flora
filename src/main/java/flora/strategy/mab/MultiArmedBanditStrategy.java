package flora.strategy.mab;

import static java.util.stream.Collectors.joining;

import flora.Knob;
import flora.Strategy;
import flora.knob.Knobs;
import java.util.Map;

/**
 * A {@link Strategy} that implements the MAB algorithm through configuration weighting. In this
 * implementation, larger weights are better.
 */
public final class MultiArmedBanditStrategy implements Strategy {
  private final MultiArmedBandit bandit;
  private final MultiArmedBanditContext context;
  private final ExplorationPolicy explorationPolicy;
  private final ExploitationPolicy exploitationPolicy;

  public MultiArmedBanditStrategy(
      MultiArmedBandit bandit,
      ExplorationPolicy explorationPolicy,
      ExploitationPolicy exploitationPolicy) {
    this.bandit = bandit;
    this.context = new MultiArmedBanditContext(bandit.getKnobs());
    this.explorationPolicy = explorationPolicy;
    this.exploitationPolicy = exploitationPolicy;
  }

  /** Check if we need to explore. Otherwise, exploit. */
  @Override
  public final Map<String, Knob> nextConfiguration() {
    if (explorationPolicy.doExplore(context)) {
      return explorationPolicy.explore(context);
    } else {
      return exploitationPolicy.exploit(context);
    }
  }

  /** Adds to the total reward for the configuration. */
  @Override
  public final void update(Map<String, Knob> knobs, Map<String, Double> measurement) {
    double reward = bandit.reward(knobs, measurement);
    context.rewards.putIfAbsent(knobs, reward);
    context.rewards.computeIfPresent(knobs, (k, oldReward) -> oldReward + reward);
    context.rewardCount.putIfAbsent(knobs, 0);
    context.rewardCount.computeIfPresent(knobs, (k, rewardCount) -> rewardCount + 1);
  }

  /** Returns the context. */
  public final MultiArmedBanditContext getContext() {
    return context;
  }

  /** Writes the knobs and rewards as a json list of dicts. */
  @Override
  public String toString() {
    return String.format(
        "{\"strategy\":\"multi_armed_bandit\",\"data\":[%s]}",
        getContext().getConfigurations().stream()
            .map(
                knobs ->
                    String.format(
                        "{\"knobs\":%s,\"measurements\":{\"count\":%d,\"reward\":%f}}",
                        Knobs.valuesToJson(knobs),
                        getContext().getRewardCount(knobs),
                        getContext().getReward(knobs)))
            .collect(joining(",")));
  }
}
