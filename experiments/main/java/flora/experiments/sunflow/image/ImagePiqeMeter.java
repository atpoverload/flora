package flora.experiments.sunflow.image;

import flora.experiments.sunflow.image.PiqeServiceGrpc.PiqeServiceBlockingStub;
import flora.meter.SnapshotMeter;
import io.grpc.ManagedChannelBuilder;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;

/** A {@link SnapshotMeter} that computes the mse image distance with a reference image. */
public final class ImagePiqeMeter extends SnapshotMeter {
  private final BufferedImageDisplay display;
  private final PiqeServiceBlockingStub piqe;
  private final double constraint;

  public final ArrayList<BufferedImage> images = new ArrayList<>();

  public ImagePiqeMeter(BufferedImageDisplay display, double constraint) {
    this.display = display;
    ManagedChannelBuilder<?> channelBuilder =
        ManagedChannelBuilder.forAddress("localhost", 8913).usePlaintext();
    this.constraint = constraint;
    this.piqe = PiqeServiceGrpc.newBlockingStub(channelBuilder.build());
  }

  /** Computes the mse with the reference. */
  @Override
  public double read() {
    BufferedImage image = display.getImage();
    images.add(image);
    ComputePiqeRequest.Builder request =
        ComputePiqeRequest.newBuilder().setWidth(image.getWidth()).setHeight(image.getHeight());
    Raster raster = image.getRaster();
    for (int j = 0; j < request.getHeight(); j++) {
      request.addImageRowBuilder();
      for (int i = 0; i < request.getWidth(); i++) {
        request.getImageRowBuilder(j).addPixel(raster.getSample(j, i, 0));
      }
    }
    double score = piqe.computePiqe(request.build()).getScore();
    if (constraint < score) {
      throw new ImageQualityFault(score, constraint);
    }
    return score;
  }
}
