package flora.meter;

import static flora.util.LoggerUtil.getLogger;

import flora.Meter;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/** A {@link Meter} that records elapsed time. */
public final class Stopwatch implements Meter {
  private static final Logger logger = getLogger();

  private final AtomicReference<Optional<Instant>> start = new AtomicReference<>(Optional.empty());
  private final AtomicReference<Optional<Duration>> duration =
      new AtomicReference<>(Optional.empty());

  /** Grabs the start time. */
  @Override
  public void start() {
    if (!start.get().isEmpty()) {
      logger.info(
          String.format(
              "ignoring start request for %s while running", this.getClass().getSimpleName()));
      return;
    }
    start.set(Optional.of(Instant.now()));
  }

  /** Sets the elapsed time between the last start and now. */
  @Override
  public void stop() {
    if (start.get().isEmpty()) {
      logger.info(
          String.format(
              "ignoring stop request for %s while not running", this.getClass().getSimpleName()));
      return;
    }
    duration.set(Optional.of(Duration.between(start.get().get(), Instant.now())));
    start.set(Optional.empty());
  }

  /** Returns the stored elapsed time as seconds with nano precision. */
  @Override
  public double read() {
    if (duration.get().isEmpty() && start.get().isEmpty()) {
      logger.info(String.format("reading an unused %s returns 0", this.getClass().getSimpleName()));
      return 0;
    } else if (start.get().isPresent()) {
      logger.info(
          String.format(
              "reading a running %s returns an instaneous value", this.getClass().getSimpleName()));
      Duration elapsed = Duration.between(start.get().get(), Instant.now());
      long seconds = elapsed.getSeconds();
      long nano = elapsed.getNano();
      return seconds + (double) nano / 10E8;
    }
    long seconds = duration.get().get().getSeconds();
    long nano = duration.get().get().getNano();
    return seconds + (double) nano / 10E8;
  }
}
