package flora.strategy.mab;

import flora.Knob;
import java.util.Map;

/** An interface that provides information for a MAB. */
public interface MultiArmedBandit {
  /** Returns the bandit's knobs. */
  Map<String, Knob> getKnobs();

  /** Returns the reward, usually in the form of a score or fitness. */
  double reward(Map<String, Knob> knobs, Map<String, Double> measurement);
}
