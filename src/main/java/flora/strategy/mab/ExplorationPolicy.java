package flora.strategy.mab;

import flora.Knob;
import java.util.Map;

/** An interface for a policy that tries to determine if we should explore configurations. */
public interface ExplorationPolicy {
  /** Returns whether we should explore or exploit. */
  boolean doExplore(MultiArmedBanditContext context);

  /** Returns a knob configuration to explore. */
  Map<String, Knob> explore(MultiArmedBanditContext context);
}
