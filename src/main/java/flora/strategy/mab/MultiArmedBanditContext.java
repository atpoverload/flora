package flora.strategy.mab;

import flora.Knob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A class that captures the context of a {@link MultiArmedBandit}. */
public final class MultiArmedBanditContext {
  final HashMap<Map<String, Knob>, Integer> rewardCount = new HashMap<>();
  final HashMap<Map<String, Knob>, Double> rewards = new HashMap<>();

  private final Map<String, Knob> knobs;

  MultiArmedBanditContext(Map<String, Knob> knobs) {
    this.knobs = knobs;
  }

  /** Returns the bandit's knobs. */
  public final Map<String, Knob> getKnobs() {
    return new HashMap<>(knobs);
  }

  /** Return all rewarded configurations. */
  public final List<Map<String, Knob>> getConfigurations() {
    return new ArrayList<>(rewardCount.keySet());
  }

  /** Returns the number of rewards given to the configuration. */
  public final int getRewardCount(Map<String, Knob> knobs) {
    return rewardCount.get(knobs);
  }

  /** Returns the total reward for the configuration. */
  public final double getReward(Map<String, Knob> knobs) {
    return rewards.get(knobs);
  }

  /** Returns the average reward for the configuration. */
  public final double getAverageReward(Map<String, Knob> knobs) {
    return rewards.get(knobs) / rewardCount.get(knobs);
  }

  /** Return the total number of rewards given. */
  public final int getTotalRewardCount() {
    return rewardCount.values().stream().mapToInt(Integer::intValue).sum();
  }

  /** Return the total reward over all configurations. */
  public final double getTotalReward() {
    return rewards.values().stream().mapToDouble(Double::doubleValue).sum();
  }
}
