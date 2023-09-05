package eflect;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Stream;

/** A {@link Future} that allows for collecting data from a {@link Supplier}. */
public final class SamplingFuture<T> implements Future<List<T>> {
  /** Start a {@link SamplingFuture} that samples at a fixed {@link Duration}. */
  public static <T> SamplingFuture<T> fixedPeriod(
      Supplier<? extends T> source, Duration period, ScheduledExecutorService executor) {
    return new SamplingFuture<>(source, period, executor);
  }

  /** Start a {@link SamplingFuture} that samples at a fixed millisecond period. */
  public static <T> SamplingFuture<T> fixedPeriodMillis(
      Supplier<? extends T> source, int periodMillis, ScheduledExecutorService executor) {
    return new SamplingFuture<>(source, Duration.ofMillis(periodMillis), executor);
  }

  /** Reduces multiple sampling futures into a single one. */
  public static <T> Iterable<T> flatten(Collection<SamplingFuture<T>> data) {
    return data.stream()
        .flatMap(
            future -> {
              try {
                return future.get().stream();
              } catch (Exception e) {
                return Stream.empty();
              }
            })
        .collect(toList());
  }

  // we can make this support more complex schedules i think
  private final Duration period;
  private final ScheduledExecutorService executor;

  private final AtomicBoolean isCollecting = new AtomicBoolean(true);
  private final List<T> collectedData = new ArrayList<>();

  private List<Future<Optional<? extends T>>> dataFutures = new ArrayList<>();

  private SamplingFuture(
      Supplier<? extends T> source, Duration period, ScheduledExecutorService executor) {
    this.period = period;
    this.executor = executor;
    synchronized (dataFutures) {
      // TODO(timur): there is a very strange failure case here. if the source throws an exception,
      // i can't catch it and it just get propagated to the caller of get
      dataFutures.add(executor.submit(() -> collectDataAndReschedule(source)));
    }
  }

  /** Stops collecting data. If {@code mayInterruptIfRunning} is true, the data is extracted. */
  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    // this will kill all pending futures
    isCollecting.set(false);
    if (mayInterruptIfRunning) {
      extractAllData();
    }
    return true;
  }

  /** Cancels the future if we are collecting and then returns the collected data. */
  @Override
  public List<T> get() {
    if (isCollecting.get()) {
      cancel(true);
    }
    return collectedData;
  }

  /** Delegates to {@code get}. */
  @Override
  public List<T> get(long timeout, TimeUnit unit) {
    return get();
  }

  /** Returns if more data will be scheduled to be collected. */
  @Override
  public boolean isCancelled() {
    return !isCollecting.get() || executor.isShutdown();
  }

  /** Returns if data is still being collected. */
  @Override
  public boolean isDone() {
    synchronized (dataFutures) {
      return isCancelled() && dataFutures.stream().allMatch(Future::isDone);
    }
  }

  /**
   * Collect from the {@link Supplier}, re-schedule for the next period start, and return the data.
   */
  private Optional<? extends T> collectDataAndReschedule(Supplier<? extends T> source) {
    if (isCancelled()) {
      isCollecting.set(false);
      return Optional.empty();
    }

    // TODO(timur): need some sort of safety mechanism so this doesn't kill the chain on throw
    Instant start = Instant.now();
    T data = source.get();
    Duration rescheduleTime = period.minus(Duration.between(start, Instant.now()));

    if (!isCancelled()) {
      synchronized (dataFutures) {
        if (rescheduleTime.toNanos() > 0) {
          // if we have some extra time, schedule the next one in the future
          dataFutures.add(
              executor.schedule(
                  () -> collectDataAndReschedule(source), rescheduleTime.toNanos(), NANOSECONDS));
        } else {
          // if we don't, run the next one immediately
          dataFutures.add(executor.submit(() -> collectDataAndReschedule(source)));
        }
      }
    }
    return Optional.of(data);
  }

  /** Wait until {@link Futures} in {@code dataFutures} are done, then extract all the data. */
  private void extractAllData() {
    // TODO(timur): should i do this with lock.notify()?
    while (!isDone()) {
      try {
        Thread.sleep(period.toMillis());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    synchronized (collectedData) {
      dataFutures
          .stream()
          .map(this::extractValue)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .forEach(collectedData::add);
      dataFutures.clear();
    }
  }

  /**
   * Forcibly retrieve the value from a {@link Future}; if it can't be retrieved, then return an
   * empty {@link Optional}.
   */
  private Optional<? extends T> extractValue(Future<Optional<? extends T>> future) {
    try {
      return future.get();
    } catch (Exception e) {
      System.out.println("could not consume a future");
    }
    return Optional.empty();
  }
}
