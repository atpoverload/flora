package flora.experiments.rendering;

import flora.Machine;
import flora.Meter;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

public final class RenderingScoreMachine extends Machine {
  private final BlockingQueue<RenderingScore> score;
  private final Map<String, Meter> meters;

  public RenderingScoreMachine(BlockingQueue<RenderingScore> score) {
    this.score = score;
    this.meters =
        Map.of(
            "energy",
            new RenderingScoreMeter(
                () -> {
                  try {
                    return score.take().getEnergy();
                  } catch (Exception e) {
                    return 0.0;
                  }
                }));
  }

  @Override
  public Map<String, Meter> meters() {
    return meters;
  }

  private static class RenderingScoreMeter implements Meter {
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
}
