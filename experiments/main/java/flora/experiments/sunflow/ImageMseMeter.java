package flora.experiments.sunflow;

import flora.meter.SnapshotMeter;
import java.awt.image.BufferedImage;

/** A {@link SnapshotMeter} that computes the mse image distance with a reference image. */
public final class ImageMseMeter extends SnapshotMeter {
  private final BufferedImageDisplay display;
  private final BufferedImage reference;

  public ImageMseMeter(BufferedImageDisplay display, BufferedImage reference) {
    this.display = display;
    this.reference = reference;
  }

  /** Computes the mse with the reference. */
  @Override
  public double read() {
    return ImageUtils.mseWithReference(display.getImage(), reference);
  }
}
