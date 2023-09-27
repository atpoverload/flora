package flora.meter;

import static flora.util.LoggerUtil.getLogger;

import flora.Meter;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

/** A {@link Meter} that records elapsed time. */
public final class Stopwatch implements Meter {
  private static final Logger logger = getLogger();

  private static final double nanoTime(Instant start, Instant end) {
    Duration elapsed = Duration.between(start, end);
    long seconds = elapsed.getSeconds();
    long nano = elapsed.getNano();
    return seconds + (double) nano / 10E8;
  }

  private boolean isRunning = false;
  private Instant start;
  private Instant end;

  /** Grabs the start time. */
  @Override
  public void start() {
    if (isRunning) {
      logger.fine(
          String.format("ignoring start for %s while running", this.getClass().getSimpleName()));
      return;
    }
    isRunning = true;
    start = Instant.now();
  }

  /** Sets the elapsed time between the last start and now. */
  @Override
  public void stop() {
    if (!isRunning) {
      logger.fine(
          String.format("ignoring stop for %s while stopped", this.getClass().getSimpleName()));
      return;
    }
    end = Instant.now();
    isRunning = false;
  }

  /** Returns the stored elapsed time as seconds with nano precision. */
  @Override
  public double read() {
    if (start == null && end == null) {
      logger.fine(String.format("reading unused %s returns 0", this.getClass().getSimpleName()));
      return 0;
    } else if (end == null) {
      logger.fine(
          String.format(
              "reading while running %s returns instaneous time", this.getClass().getSimpleName()));
      return nanoTime(start, Instant.now());
    }
    return nanoTime(start, end);
  }
}
