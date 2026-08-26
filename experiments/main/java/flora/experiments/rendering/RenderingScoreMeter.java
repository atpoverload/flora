package flora.experiments.rendering;

import flora.Meter;
import java.util.Map;
import java.util.function.Supplier;

public class RenderingScoreMeter implements Meter {
  private final Supplier<Double> score;

  RenderingScoreMeter(Supplier<Double> score) {
    this.score = score;
  }

  @Override
  public void start() {}

  @Override
  public void stop() {}

  @Override
  public double read() {
    return score.get();
  }
}
