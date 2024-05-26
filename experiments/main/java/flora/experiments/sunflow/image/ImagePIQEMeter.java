package flora.experiments.sunflow.image;

import flora.experiments.sunflow.image.PIQEServiceGrpc.PIQEServiceBlockingStub;
import flora.meter.SnapshotMeter;
import io.grpc.ManagedChannelBuilder;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;

/** A {@link SnapshotMeter} that computes the mse image distance with a reference image. */
public final class ImagePIQEMeter extends SnapshotMeter {
  private final BufferedImageDisplay display;
  private final PIQEServiceBlockingStub piqe;

  public final ArrayList<BufferedImage> images = new ArrayList<>();

  public ImagePIQEMeter(BufferedImageDisplay display) {
    this.display = display;
    ManagedChannelBuilder<?> channelBuilder =
        ManagedChannelBuilder.forAddress("localhost", 8980).usePlaintext();
    this.piqe = PIQEServiceGrpc.newBlockingStub(channelBuilder.build());
  }

  /** Computes the mse with the reference. */
  @Override
  public double read() {
    BufferedImage image = display.getImage();
    ComputePiqeRequest.Builder request =
        ComputePiqeRequest.newBuilder().setWidth(image.getWidth()).setHeight(image.getHeight());
    Raster raster = image.getRaster();
    for (int j = 0; j < request.getHeight(); j++) {
      request.addImageRowBuilder();
      for (int i = 0; i < request.getWidth(); i++) {
        request.getImageRowBuilder(j).addPixel(raster.getSample(j, i, 0));
      }
    }
    return piqe.computePiqe(request.build()).getScore();
  }
}
