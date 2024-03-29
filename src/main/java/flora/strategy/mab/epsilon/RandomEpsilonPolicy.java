package flora.strategy.mab.epsilon;

import flora.strategy.mab.MultiArmedBandit;

/** A {@link EpsilonPolicy} that randomizes knob settings for exploration. */
public final class RandomEpsilonPolicy extends EpsilonPolicy {
  public RandomEpsilonPolicy(double epsilon, EpsilonPolicyKind policyKind) {
    super(epsilon, policyKind);
  }

  public RandomEpsilonPolicy(double epsilon, String policyKind) {
    super(epsilon, policyKind);
  }

  /** Selects a random configuration of knobs. */
  @Override
  public <K, C, MAB extends MultiArmedBandit<K, C, MAB>> MAB explore(MAB bandit) {
    return bandit.random();
  }
}
