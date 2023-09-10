package flora.strategy.mab;

import flora.context.RandomizableContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A class that captures the context of a {@link MultiArmedBandit}. */
public abstract class MultiArmedBandit<K, C, KC extends MultiArmedBandit<K, C, KC>>
    implements RandomizableContext<K, C, KC> {
  // TODO: should be changed to more efficient
  private final HashMap<C, Integer> rewardedCount = new HashMap<>();
  private final HashMap<C, Double> rewards = new HashMap<>();

  private C currentConfiguration;

  @Override
  public final C configuration() {
    return currentConfiguration;
  }

  @SuppressWarnings("unchecked")
  public final KC setConfiguration(C configuration) {
    currentConfiguration = configuration;
    return (KC) this;
  }

  protected abstract double reward(C configuration, Map<String, Double> measurement);

  /** Grabs the reward from the inputs and adds it to the storage. */
  final void addConfiguration(MultiArmedBandit<K, C, KC> context, Map<String, Double> measurement) {
    double reward = reward(context.configuration(), measurement);
    rewards.putIfAbsent(context.configuration(), reward);
    rewards.computeIfPresent(context.configuration(), (c, oldReward) -> oldReward + reward);
    rewardedCount.putIfAbsent(context.configuration(), 0);
    rewardedCount.computeIfPresent(context.configuration(), (c, oldCount) -> oldCount + 1);
  }

  /** Return all rewarded configurations. */
  public final List<C> rewardedConfigurations() {
    return new ArrayList<>(rewardedCount.keySet());
  }

  /** Returns the number of rewards given to the configuration. */
  public final int rewardedCount(C configuration) {
    return rewardedCount.get(configuration);
  }

  /** Returns the total reward for the configuration. */
  public final double reward(C configuration) {
    return rewards.get(configuration);
  }

  /** Returns the average reward for the configuration. */
  public final double averageReward(C configuration) {
    return rewards.get(configuration) / rewardedCount.get(configuration);
  }

  /** Return the total number of rewards given. */
  public final int totalRewardedCount() {
    return rewardedCount.values().stream().mapToInt(Integer::intValue).sum();
  }

  /** Return the total reward over all configurations. */
  public final double totalReward() {
    return rewards.values().stream().mapToDouble(Double::doubleValue).sum();
  }
}
