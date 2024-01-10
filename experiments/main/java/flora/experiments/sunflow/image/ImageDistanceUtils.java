package flora.experiments.sunflow.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

/** Utility to compute image distances for images of different sizes. */
public final class ImageDistanceUtils {
  /**
   * Resizes an image to the given dimensions. implementation derived from
   * https://stackoverflow.com/questions/4216123/how-to-scale-a-bufferedimage
   */
  public static BufferedImage resize(BufferedImage source, int width, int height) {
    BufferedImage resized = new BufferedImage(width, height, source.getType());
    Graphics2D g = resized.createGraphics();
    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.drawImage(source, 0, 0, width, height, 0, 0, source.getWidth(), source.getHeight(), null);
    g.dispose();
    return resized;
  }

  /** Computes the mse of two images (which must have the same dimensions and type). */
  public static double mse(BufferedImage im1, BufferedImage im2) {
    if (im1.getType() != im2.getType()
        && im1.getHeight() != im2.getHeight()
        && im1.getWidth() != im2.getWidth()) {
      throw new IllegalArgumentException(
          String.format("images to compare do not match: %s, %s", im1, im2));
    }

    double mse = 0;
    int width = im1.getWidth();
    int height = im1.getHeight();
    Raster r1 = im1.getRaster();
    Raster r2 = im2.getRaster();
    for (int j = 0; j < height; j++)
      for (int i = 0; i < width; i++)
        mse += Math.pow(r1.getSample(i, j, 0) - r2.getSample(i, j, 0), 2);
    mse /= (double) (width * height);
    return mse;
  }

  /** Computes the psnr of two images (which must have the same dimensions and type). */
  public static double psnr(BufferedImage im1, BufferedImage im2) {
    double mse = mse(im1, im2);
    int maxVal = 255;
    double x = Math.pow(maxVal, 2) / mse;
    double psnr = 10.0 * logbase10(x);
    return psnr;
  }

  /** Computes the mse where the {@code image} is resized to the {@code reference}. */
  public static double mseWithReference(BufferedImage image, BufferedImage reference) {
    return mse(resize(image, reference.getWidth(), reference.getHeight()), reference);
  }

  /** Computes the psnr where the {@code image} is resized to the {@code reference}. */
  public static double psnrWithReference(BufferedImage image, BufferedImage reference) {
    return psnr(resize(image, reference.getWidth(), reference.getHeight()), reference);
  }

  private static double logbase10(double x) {
    return Math.log(x) / Math.log(10);
  }

  private ImageDistanceUtils() {}
}
