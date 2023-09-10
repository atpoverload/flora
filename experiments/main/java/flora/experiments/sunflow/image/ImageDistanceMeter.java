package flora.experiments.sunflow.image;

import flora.meter.SnapshotMeter;
import java.awt.image.BufferedImage;

/** A {@link SnapshotMeter} that computes the mse image distance with a reference image. */
public final class ImageDistanceMeter extends SnapshotMeter {
  private final BufferedImageDisplay display;
  private final BufferedImage reference;
  private final ImageDistanceScore score;

  public ImageDistanceMeter(
      BufferedImageDisplay display, BufferedImage reference, ImageDistanceScore score) {
    this.display = display;
    this.reference = reference;
    this.score = score;
  }

  /** Computes the mse with the reference. */
  @Override
  public double read() {
    return score.score(display.getImage(), reference);
  }
}
