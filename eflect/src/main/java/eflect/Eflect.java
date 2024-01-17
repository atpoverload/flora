package eflect;

import static eflect.util.ProcUtil.readProcStat;
import static eflect.util.ProcUtil.readTaskStats;

import eflect.data.AccountantMerger;
import eflect.data.EnergyAccountant;
import eflect.data.EnergyFootprint;
import eflect.data.EnergySample;
import eflect.data.Sample;
import eflect.data.SampleProcessor;
import eflect.data.jiffies.JiffiesAccountant;
import eflect.data.jiffies.ProcStatSample;
import eflect.data.jiffies.ProcTaskSample;
import eflect.util.Powercap;
import eflect.util.Rapl;
import eflect.util.SamplingFuture;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

/** An object that will concurrently produce {@link EnergyFootprints}. */
public final class Eflect {
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

  public static Eflect raplEflect(
      int periodMillis, ScheduledExecutorService executor, int mergeAttempts) {
    return new Eflect(
        periodMillis,
        executor,
        List.of(
            () -> new ProcStatSample(Instant.now(), readProcStat()),
            () -> new ProcTaskSample(Instant.now(), readTaskStats()),
            () -> new EnergySample(Instant.now(), Rapl.getInstance().getEnergyStats())),
        new AccountantMerger<EnergyFootprint>(
            () ->
                new EnergyAccountant(
                    Rapl.getInstance().getSocketCount(),
                    /* componentCount= */ 2,
                    /* wrapAround= */ 0,
                    new JiffiesAccountant(
                        Rapl.getInstance().getSocketCount(),
                        cpu -> cpu / (CPU_COUNT / Rapl.getInstance().getSocketCount()))),
            mergeAttempts));
  }

  public static Eflect powercapEflect(
      int periodMillis, ScheduledExecutorService executor, int mergeAttempts) {
    return new Eflect(
        periodMillis,
        executor,
        List.of(
            () -> new ProcStatSample(Instant.now(), readProcStat()),
            () -> new ProcTaskSample(Instant.now(), readTaskStats()),
            () -> new EnergySample(Instant.now(), Powercap.getEnergyStats())),
        new AccountantMerger<EnergyFootprint>(
            () ->
                new EnergyAccountant(
                    Powercap.SOCKET_COUNT,
                    /* componentCount= */ 3,
                    /* wrapAround= */ Rapl.getInstance().getWrapAroundEnergy(),
                    new JiffiesAccountant(
                        Powercap.SOCKET_COUNT, cpu -> cpu / (CPU_COUNT / Powercap.SOCKET_COUNT))),
            mergeAttempts));
  }

  private final int periodMillis;
  private final ScheduledExecutorService executor;
  private final List<Supplier<Sample>> sources;
  private final SampleProcessor<Collection<EnergyFootprint>> processor;

  private final ArrayList<SamplingFuture<Sample>> dataFutures = new ArrayList<>();
  private final ArrayList<EnergyFootprint> footprints = new ArrayList<>();

  public Eflect(
      int periodMillis,
      ScheduledExecutorService executor,
      List<Supplier<Sample>> sources,
      SampleProcessor<Collection<EnergyFootprint>> processor) {
    this.periodMillis = periodMillis;
    this.executor = executor;
    this.sources = sources;
    this.processor = processor;
  }

  public void start() {
    footprints.clear();
    for (Supplier<Sample> source : sources) {
      dataFutures.add(SamplingFuture.fixedPeriodMillis(source, periodMillis, executor));
    }
  }

  public void stop() {
    for (SamplingFuture<Sample> future : dataFutures) {
      for (Sample sample : future.get()) {
        processor.add(sample);
      }
    }
    footprints.addAll(processor.process());
  }

  public ArrayList<EnergyFootprint> read() {
    return new ArrayList<>(footprints);
  }
}
