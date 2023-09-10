package flora.strategy.mab.epsilon;

import flora.strategy.mab.ExplorationPolicy;
import flora.strategy.mab.MultiArmedBandit;

/** A greedy {@link ExplorationPolicy} that uses a factor to determine when to explore. */
public abstract class EpsilonPolicy implements ExplorationPolicy {
  public enum EpsilonPolicyKind {
    // FIRST, // explore each configuration at least once before exploiting
    GREEDY, // exploit as soon as possible
  }

  private final double epsilon;
  private final EpsilonPolicyKind policyKind;

  protected EpsilonPolicy(double epsilon, EpsilonPolicyKind policyKind) {
    this.epsilon = epsilon;
    this.policyKind = policyKind;
  }

  protected EpsilonPolicy(double epsilon, String policyKind) {
    this(epsilon, EpsilonPolicyKind.valueOf(policyKind.toUpperCase()));
  }

  /** Return whether we have sufficient data and rolled a number lower than {@code epsilon}. */
  @Override
  public final <K, C, KC extends MultiArmedBandit<K, C, KC>> boolean doExplore(KC bandit) {
    // Have to run at least once to "prime the pump"
    if (bandit.totalRewardedCount() < 1) {
      return true;
    }
    switch (this.policyKind) {
        // case FIRST:
        //   boolean triedAllConfigs =
        //       bandit.rewardedConfigurations().size() < bandit.knobs().count();
        //   return triedAllConfigs || Math.random() < epsilon;
      case GREEDY:
        return Math.random() < epsilon;
      default:
        throw new IllegalStateException(
            String.format("Unhandled enum case for policy kind %s", this.policyKind));
    }
  }
}
