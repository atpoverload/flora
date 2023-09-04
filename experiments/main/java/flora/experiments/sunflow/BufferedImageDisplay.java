package flora.experiments.sunflow;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.sunflow.core.Display;
import org.sunflow.image.Color;

// public final class BufferedImageDisplay extends SnapshotMeter implements Display {
public final class BufferedImageDisplay implements Display {
  private int[] pixels;
  private BufferedImage image;

  @Override
  public synchronized void imageBegin(int w, int h, int bucketSize) {
    pixels = new int[w * h];
    image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
  }

  @Override
  public void imagePrepare(int x, int y, int w, int h, int id) {}

  @Override
  public void imageUpdate(int x, int y, int w, int h, Color[] data, float[] alpha) {
    int iw = image.getWidth();
    int off = x + iw * y;
    iw -= w;
    for (int j = 0, index = 0; j < h; j++, off += iw)
      for (int i = 0; i < w; i++, index++, off++) pixels[off] = 0xFF000000 | data[index].toRGB();
  }

  @Override
  public void imageFill(int x, int y, int w, int h, Color c, float alpha) {
    int iw = image.getWidth();
    int off = x + iw * y;
    iw -= w;
    int rgb = 0xFF000000 | c.toRGB();
    for (int j = 0, index = 0; j < h; j++, off += iw)
      for (int i = 0; i < w; i++, index++, off++) pixels[off] = rgb;
  }

  @Override
  public synchronized void imageEnd() {
    image.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
  }

  public BufferedImage getImage() {
    BufferedImage imageCopy =
        new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
    Graphics2D graphics = imageCopy.createGraphics();
    graphics.drawImage(image, 0, 0, null);
    graphics.dispose();
    return imageCopy;
  }

  // @Override
  // public Measurement read() {
  //   BufferedImage imageCopy =
  //       new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
  //   Graphics2D graphics = imageCopy.createGraphics();
  //   graphics.drawImage(image, 0, 0, null);
  //   graphics.dispose();
  //   return imageCopy;
  // }
}
