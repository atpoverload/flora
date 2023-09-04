package flora.strategy.mab.epsilon;

import flora.Knob;
import flora.knob.Knobs;
import flora.strategy.mab.MultiArmedBanditContext;
import java.util.Map;

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
  public Map<String, Knob> explore(MultiArmedBanditContext context) {
    return Knobs.withRandomValues(context.getKnobs());
  }
}
