package flora.strategy.mab.epsilon;

import flora.strategy.mab.ExplorationPolicy;
import flora.strategy.mab.MultiArmedBandit;

/** A greedy {@link ExplorationPolicy} that uses a factor to determine when to explore. */
public abstract class EpsilonPolicy implements ExplorationPolicy {
  private static double sigmoid(int count, double alpha) {
    return 1 / (1 + Math.pow(Math.E, -count * alpha));
  }

  public enum EpsilonPolicyKind {
    GREEDY, // exploit as soon as possible
    DECREASING, // explore more initially
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
  public final <K, C, MAB extends MultiArmedBandit<K, C, MAB>> boolean doExplore(MAB bandit) {
    // Have to run at least once to "prime the pump"
    if (bandit.totalCount() < 1) {
      return true;
    }
    switch (this.policyKind) {
      case GREEDY:
        return Math.random() < epsilon;
      case DECREASING:
        return Math.random() < (epsilon * sigmoid(bandit.totalCount(), 0.0025));
      default:
        throw new IllegalStateException(
            String.format("Unhandled enum case for policy kind %s", this.policyKind));
    }
  }
}
