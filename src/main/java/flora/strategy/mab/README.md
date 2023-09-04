# `flora` Multi-Armed Bandits

A library for implementing the [Multi-Armed Bandit](https://en.wikipedia.org/wiki/Multi-armed_bandit) (MAB) problem. MAB is quite powerful because it is simple to implement and can be applied to cases easily. However, a hand-rolled MAB usually struggles with scaling when trying to parameterize problems. This library provides some pieces you can use to quickly assemble an MAB using `flora`'s model.

## The MAB API

The multi-armed bandit is a extension of a slot machine that has multiple arms. Each arm has its own payout, or *reward*, and we, as the gamer, are trying to find the arms with the best payout. This means we may need to strike a balance between *exploring* arms, i.e. trying ones we don't quite know the payout on, and *exploiting* arms, where we pull the arm we believe has the best payout.

### `MultiArmedBandit`

The `MultiArmedBandit` exposes a set of knobs and provides a reward for measured configurations.

```Java
public interface MultiArmedBandit {
  /** Returns the bandit's knobs. */
  Map<String, Knob> getKnobs();

  /** Returns the reward, usually in the form of a score or fitness. */
  double reward(Map<String, Knob> knobs, Map<String, Double> measurement);
}
```

The API views the `MultiArmedBandit` as a `MultiArmedBanditContext`, which exposes the current knowledge of the bandit.

```Java
/** A class that captures the context of a {@link MultiArmedBandit}. */
public final class MultiArmedBanditContext {
  /** Returns the bandit's knobs. */
  public final Map<String, Knob> getKnobs() { ... }

  /** Return all rewarded configurations. */
  public final List<Map<String, Knob>> getConfigurations() { ... }

  /** Returns the number of rewards given to the configuration. */
  public final int getRewardCount(Map<String, Knob> knobs) { ... }

  /** Returns the total reward for the configuration. */
  public final double getReward(Map<String, Knob> knobs) { ... }

  /** Returns the average reward for the configuration. */
  public final double getAverageReward(Map<String, Knob> knobs) { ... }

  /** Return the total number of rewards given. */
  public final int getTotalRewardCount() { ... }

  /** Return the total reward over all configurations. */
  public final double getTotalReward() { ... }
}
```

### `ExplorationPolicy` and `ExploitationPolicy`

Decisions can be made on `MultiArmedBanditContexts` through exploration and exploitation policies.

```Java
/** An interface for a policy that tries to determine if we should explore configurations. */
public interface ExplorationPolicy {
  /** Returns whether we should explore or exploit. */
  boolean doExplore(MultiArmedBanditContext context);

  /** Returns a knob configuration to explore. */
  Map<String, Knob> explore(MultiArmedBanditContext context);
}
```

```Java
/** An interface for a policy that tries to pick the best configuration. */
public interface ExploitationPolicy {
  /** Returns a knob configuration to exploit. */
  Map<String, Knob> exploit(MultiArmedBanditContext context);
}
```

Policies are a key component of finding good configurations. Policy selection is so important that carefully designed policies can result in considerable improvements. `flora` provides the following policies out of the box:

Exploration Policies
 - `epsilon` greedy

Exploitation Policies
  - `HighestConfiguration`
  - `LowestConfiguration`
  - `NearestTo`
  - `NearestToMean`
  - `SmallestAbove`
  - `LargestBelow`


### `MultiArmedBanditStrategy`

The `MultiArmedBanditStrategy` implements the MAB algorithm by using an `ExplorationPolicy` and `ExploitationPolicy` to choose configurations, and a `MultiArmedBandit` and a context to reward configurations. Configurations are chosen by asking the policies whether we need to explore from the bandit context.

```Java
/** Check if we need to explore. Otherwise, exploit. */
@Override
public final Map<String, Knob> nextConfiguration() {
  if (explorationPolicy.doExplore(context)) {
    return explorationPolicy.explore(context);
  } else {
    return exploitationPolicy.exploit(context);
  }
}
```

When the strategy is updated, it asks the bandit for a reward and adds it to the accumulated reward for the configuration.

```Java
/** Adds to the total reward for the configuration. */
@Override
public final void update(Map<String, Knob> knobs, Map<String, Double> measurement) {
  double reward = bandit.reward(knobs, measurement);
  context.rewards.putIfAbsent(knobs, reward);
  context.rewards.computeIfPresent(knobs, (k, oldReward) -> oldReward + reward);
  context.rewardCounts.putIfAbsent(knobs, 0);
  context.rewardCounts.computeIfPresent(knobs, (k, evals) -> evals + 1);
}
```

### Assembling a bandit stratgy

A bandit strategy can be constructed by implementing a bandit and providing selection policies:

```Java
Knob xBounds = new IntRangeKnob(xMin, xMax);
Knob yBounds = new IntRangeKnob(yMin, yMax);
Knob zBounds = new IntRangeKnob(zMin, zMax);
Map<String, Knob> knobs = Map.of("X", xBounds, "Y", yBounds, "Z", zBounds);
Strategy stategy = new MultiArmedBanditStrategy(
      new MultiArmedBandit() {
        @Override
        public void getKnobs() {
          return knobs;
        }

        @Override
        public double reward(Map<String, Knob> knobs, Map<String, Double> measure) {
          return Math.sqrt(Math.pow(measure.get("U"), 2) + Math.pow(measure.get("V"), 2) + Math.pow(measure.get("W"), 2));
        }
      },
      new RandomEpsilonPolicy(0.10, "first"),
      WorstConfiguration.getPolicy());
```

A more worked example can be found in [`flora.examples.fibonacci`]().
