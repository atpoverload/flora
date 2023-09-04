package flora.experiments.sunflow;

import flora.Knob;
import flora.strategy.mab.MultiArmedBandit;
import java.util.Map;

/** A {@link MultiArmedBandit} whose reward is the mse from a reference image. */
public class MseBandit implements MultiArmedBandit {
  private final Map<String, Knob> knobs;

  public MseBandit(Map<String, Knob> knobs) {
    this.knobs = knobs;
  }

  /** Returns the bandit's knobs. */
  @Override
  public Map<String, Knob> getKnobs() {
    return knobs;
  }

  /** Returns the mse measurement. */
  @Override
  public double reward(Map<String, Knob> knobs, Map<String, Double> measurement) {
    return measurement.get("mse");
  }
}
