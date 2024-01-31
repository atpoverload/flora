package flora.experiments.sunflow.image;

import flora.PerformanceFault;

public final class ImageQualityFault extends PerformanceFault {
  private final double score;
  private final double constraint;

  public ImageQualityFault(double score, double constraint) {
    this.score = score;
    this.constraint = constraint;
  }

  @Override
  public String description() {
    return String.format("{\"score\":%f,\"constraint\":%f", score, constraint);
  }
}
