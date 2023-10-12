package flora.experiments.sunflow.image;

import flora.experiments.sunflow.RenderingConfiguration;
import flora.experiments.sunflow.RenderingKnobs;
import flora.strategy.mab.MultiArmedBandit;
import java.util.Map;

/** A {@link MultiArmedBandit} whose reward is the distance from a reference image. */
public final class ImageDistanceBandit
    extends MultiArmedBandit<RenderingKnobs, RenderingConfiguration, ImageDistanceBandit> {
  private final RenderingKnobs knobs = RenderingKnobs.defaultKnobs();
  private final ImageDistanceScore score;

  public ImageDistanceBandit(ImageDistanceScore score) {
    withConfiguration(RenderingConfiguration.defaultConfiguration());
    this.score = score;
  }

  /** Returns the bandit's knobs. */
  @Override
  public RenderingKnobs knobs() {
    return knobs;
  }

  /** Returns the mse measurement. */
  @Override
  public double reward(ImageDistanceBandit bandit, Map<String, Double> measurement) {
    return measurement.get(score.name());
  }

  /** Randomizes the configuration from the knobs. */
  @Override
  public ImageDistanceBandit random() {
    withConfiguration(knobs.randomConfiguration());
    return this;
  }
}
