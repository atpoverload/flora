package eflect;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;
import java.util.stream.Stream;

/** An eflect that samples this process concurrently. */
public final class LocalEflect implements Eflect {
  private static final Logger logger = LoggerUtil.getLogger();

  private final Duration period;
  private final ScheduledExecutorService executor;

  private final ArrayList<SamplingFuture<?>> dataFutures = new ArrayList<>();
  private final ArrayList<Virtualization> data = new ArrayList<>();

  private boolean isRunning = false;

  public LocalEflect(Duration period, ScheduledExecutorService executor) {
    if (period.equals(Duration.ZERO)) {
      throw new IllegalArgumentException("cannot sample with a period of " + period);
    }
    this.period = period;
    this.executor = executor;
  }

  public LocalEflect(long periodMillis, ScheduledExecutorService executor) {
    this(Duration.ofMillis(periodMillis), executor);
  }

  /** Starts collection of energy data and purges the old data. */
  @Override
  public void start() {
    synchronized (this) {
      if (isRunning) {
        logger.info("ignoring start request while sampling");
        return;
      }

      // make sure we have an executor
      if (executor.isShutdown()) {
        logger.info(
            String.format("ignoring start request while executor %s is shutdown", executor));
        return;
      }

      // TODO: this is a strange potential failure if we forgot to read
      if (!dataFutures.isEmpty()) {
        logger.info("data has not been read; retrieving it now for safety");
        this.read();
      }

      // start a new collection
      data.clear();
      dataFutures.clear();
      dataFutures.add(SamplingFuture.fixedPeriod(Powercap::sample, period, executor));
      dataFutures.add(SamplingFuture.fixedPeriod(CpuJiffies::sample, period, executor));
      dataFutures.add(SamplingFuture.fixedPeriod(TaskJiffies::sampleSelf, period, executor));
      logger.info(String.format("started sampling at %s", this.period));
      isRunning = true;
    }
  }

  /** Stops the collection. */
  @Override
  public void stop() {
    synchronized (this) {
      if (!isRunning) {
        logger.info("ignoring stop request while not sampling");
        return;
      }
      isRunning = false;
      dataFutures.forEach(future -> future.cancel(true));
      logger.info("stopped sampling");
    }
  }

  /** Returns the data from the last session. */
  @Override
  public List<Virtualization> read() {
    if (isRunning) {
      logger.info("ignoring read request while running");
      return new ArrayList<>();
    }

    synchronized (this) {
      if (!this.dataFutures.isEmpty()) {
        EflectDataSet.Builder dataSet = EflectDataSet.newBuilder();
        dataFutures.stream()
            .flatMap(
                future -> {
                  try {
                    return future.get().stream();
                  } catch (Exception e) {
                    return Stream.empty();
                  }
                })
            .forEach(
                sample -> {
                  if (sample instanceof PowercapSample) {
                    dataSet.addPowercap((PowercapSample) sample);
                  } else if (sample instanceof CpuSample) {
                    dataSet.addCpu((CpuSample) sample);
                  } else if (sample instanceof TaskSample) {
                    dataSet.addTask((TaskSample) sample);
                  }
                });
        dataFutures.clear();

        List<Virtualization> activity =
            JiffiesVirtualizer.virtualize(
                dataSet.getCpuList(), dataSet.getTaskList(), period.toMillis());
        List<Virtualization> energy =
            PowercapVirtualizer.virtualize(dataSet.getPowercapList(), activity, period.toMillis());
        this.data.clear();
        this.data.addAll(energy);
      }
    }
    return new ArrayList<>(this.data);
  }
}
