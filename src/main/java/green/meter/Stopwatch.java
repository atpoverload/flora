package green.meter;

import green.Meter;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/** A {@link Meter} that records elapsed time. */
public final class Stopwatch implements Meter {
  private final AtomicReference<Optional<Instant>> start = new AtomicReference<>(Optional.empty());
  private final AtomicReference<Optional<Duration>> duration =
      new AtomicReference<>(Optional.empty());

  /** Grabs the start time. */
  @Override
  public void start() {
    if (!start.get().isEmpty()) {
      throw new IllegalStateException("A stopwatch is being started without being stopped!");
    }
    start.set(Optional.of(Instant.now()));
  }

  /** Sets the elapsed time between the last start and now. */
  @Override
  public void stop() {
    if (start.get().isEmpty()) {
      throw new IllegalStateException("A stopwatch is being stopped without being started!");
    }
    duration.set(Optional.of(Duration.between(start.get().get(), Instant.now())));
    start.set(Optional.empty());
  }

  /** Returns the stored elapsed time as seconds with nano precision. */
  @Override
  public double read() {
    if (duration.get().isEmpty() && start.get().isEmpty()) {
      throw new IllegalStateException("A stopwatch is being read without being started!");
    } else if (start.get().isPresent()) {
      throw new IllegalStateException("A stopwatch is being read without being stopped!");
    }
    long seconds = duration.get().get().getSeconds();
    long nano = duration.get().get().getNano();
    return seconds + (double) nano / 10E9;
  }
}
