package flora.experiments.sunflow.image;

import java.awt.image.BufferedImage;

/** Enum for the scoring algorithms for comparing {@link BufferedImages}. */
public enum ImageDistanceScore {
  MSE,
  PSNR;

  public double score(BufferedImage image, BufferedImage reference) {
    switch (this) {
      case MSE:
        return ImageDistanceUtils.mseWithReference(image, reference);
      case PSNR:
        return ImageDistanceUtils.psnrWithReference(image, reference);
      default:
        throw new IllegalStateException(
            String.format("Unhandled %s value %s", this.getClass(), this));
    }
  }
}
