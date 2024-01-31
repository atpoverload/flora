package flora.experiments.sunflow.image;

import flora.meter.SnapshotMeter;
import java.awt.image.BufferedImage;

/** A {@link SnapshotMeter} that computes the mse image distance with a reference image. */
public final class ConstrainedImageDistanceMeter extends SnapshotMeter {
  private final BufferedImageDisplay display;
  private final BufferedImage reference;
  private final ImageDistanceScore score;
  private final double constraint;

  public ConstrainedImageDistanceMeter(
      BufferedImageDisplay display,
      BufferedImage reference,
      ImageDistanceScore score,
      double constraint) {
    this.display = display;
    this.reference = reference;
    this.score = score;
    this.constraint = constraint;
  }

  /** Computes the mse with the reference. */
  @Override
  public double read() {
    BufferedImage image = display.getImage();
    double score = this.score.score(image, reference);
    if (constraint < score) {
      throw new ImageQualityFault(score, constraint);
    }
    return score;
  }
}
