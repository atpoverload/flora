package flora.strategy.mab;

import static java.util.stream.Collectors.joining;

import flora.context.RandomizableContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A context that captures the context of a {@link MultiArmedBandit} as {@link HashMap}
 * aggregations.
 */
public abstract class MultiArmedBandit<K, C, MAB extends MultiArmedBandit<K, C, MAB>>
    implements RandomizableContext<K, C, MAB> {
  // TODO: should be changed to more efficient
  private final HashMap<C, Integer> counts = new HashMap<>();
  private final HashMap<C, Double> rewards = new HashMap<>();

  private C configuration;

  protected abstract double reward(MAB context, Map<String, Double> measurement);

  /** Grabs the reward from the inputs and adds it to the storage. */
  public final void update(MAB context, Map<String, Double> measurement) {
    double reward = reward(context, measurement);
    C configuration = context.configuration();
    rewards.putIfAbsent(configuration, reward);
    rewards.computeIfPresent(configuration, (unused, oldReward) -> oldReward + reward);
    counts.putIfAbsent(configuration, 0);
    counts.computeIfPresent(configuration, (unused, oldCount) -> oldCount + 1);
  }

  @SuppressWarnings("unchecked")
  public final MAB withConfiguration(C configuration) {
    this.configuration = configuration;
    return (MAB) this;
  }

  @Override
  public final C configuration() {
    return configuration;
  }

  // TODO: this interface is a little messy
  /** Return all rewarded configurations. */
  public final List<C> rewardedConfigurations() {
    return new ArrayList<>(counts.keySet());
  }

  /** Returns the number of rewards given to the configuration. */
  public final int count(C configuration) {
    return counts.get(configuration);
  }

  /** Returns the total reward for the configuration. */
  public final double reward(C configuration) {
    return rewards.get(configuration);
  }

  /** Returns the average reward for the configuration. */
  public final double averageReward(C configuration) {
    return rewards.get(configuration) / counts.get(configuration);
  }

  /** Return the total number of rewards given. */
  public final int totalCount() {
    return counts.values().stream().mapToInt(Integer::intValue).sum();
  }

  /** Return the total reward over all configurations. */
  public final double totalReward() {
    return rewards.values().stream().mapToDouble(Double::doubleValue).sum();
  }

  /** Returns the bandit counts and total rewards as a json dict. */
  @Override
  public String toString() {
    return String.format(
        "{\"count\":{%s},\"reward\":{%s}}",
        counts.entrySet().stream()
            .map(e -> String.format("\"%s\":%d", e.getKey(), e.getValue().longValue()))
            .collect(joining(",")),
        rewards.entrySet().stream()
            .map(e -> String.format("\"%s\":{%s}", e.getKey(), averageReward(e.getKey())))
            .collect(joining(",")));
  }
}
