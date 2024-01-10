package eflect;

import static eflect.util.ProcUtil.readProcStat;
import static eflect.util.ProcUtil.readTaskStats;

import eflect.data.AccountantMerger;
import eflect.data.EnergyAccountant;
import eflect.data.EnergyFootprint;
import eflect.data.EnergySample;
import eflect.data.Sample;
import eflect.data.SampleCollector;
import eflect.data.SampleProcessor;
import eflect.data.jiffies.JiffiesAccountant;
import eflect.data.jiffies.ProcStatSample;
import eflect.data.jiffies.ProcTaskSample;
import eflect.util.Powercap;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

/** A collector that uses the eflect algorithm as a {@link SampleProcessor}. */
final class PowercapEflectCollector extends SampleCollector<Collection<EnergyFootprint>> {
  // system constants
  private static final int DOMAIN_COUNT = Powercap.SOCKET_COUNT;
  private static final int COMPONENT_COUNT = 2; // hard-coded value from powercap
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

  private static Collection<Supplier<Sample>> getSources() {
    Supplier<Sample> stat = () -> new ProcStatSample(Instant.now(), readProcStat());
    Supplier<Sample> task = () -> new ProcTaskSample(Instant.now(), readTaskStats());
    Supplier<Sample> rapl = () -> new EnergySample(Instant.now(), Powercap.getEnergyStats());
    return List.of(stat, task, rapl);
  }

  private static IntUnaryOperator getDomainConversion() {
    return cpu -> cpu / (CPU_COUNT / DOMAIN_COUNT);
  }

  PowercapEflectCollector(int mergeAttempts, ScheduledExecutorService executor, Duration period) {
    super(
        getSources(),
        new AccountantMerger<EnergyFootprint>(
            () ->
                new EnergyAccountant(
                    DOMAIN_COUNT,
                    COMPONENT_COUNT,
                    /* wrapAround= */ 0,
                    new JiffiesAccountant(DOMAIN_COUNT, getDomainConversion())),
            mergeAttempts),
        executor,
        period);
  }
}
