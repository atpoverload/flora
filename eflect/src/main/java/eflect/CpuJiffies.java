package eflect;

import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.util.Map;
import java.util.logging.Logger;

/** Helper to handle cpu jiffies from /proc/stat. */
public final class CpuJiffies {
  private static final Logger logger = LoggerUtil.getLogger();

  // system information
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
  private static final String SYSTEM_STAT_FILE = String.join(File.separator, "/proc", "stat");

  // indicies for cpu stat because there are so many
  private enum CpuIndex {
    CPU(0),
    USER(1),
    NICE(2),
    SYSTEM(3),
    IDLE(4),
    IOWAIT(5),
    IRQ(6),
    SOFTIRQ(7),
    STEAL(8),
    GUEST(9),
    GUEST_NICE(10);

    private int index;

    private CpuIndex(int index) {
      this.index = index;
    }
  }

  /** Reads the system as a {@link eflect.CpuSample}. */
  public static CpuSample sample() {
    return parseCpus(readCpus()).setTimestamp(Instant.now().toEpochMilli()).build();
  }

  /** Take the difference of two readings. Assumes the cpu is the same for both. */
  public static CpuReading difference(CpuReading first, CpuReading second) {
    return CpuReading.newBuilder()
        .setCpu(first.getCpu())
        .setUser(second.getUser() - first.getUser())
        .setNice(second.getNice() - first.getNice())
        .setSystem(second.getSystem() - first.getSystem())
        // TODO(timur): this can be used to sanity check
        .setIdle(second.getIdle() - first.getIdle())
        .setIowait(second.getIowait() - first.getIowait())
        .setIrq(second.getIrq() - first.getIrq())
        .setSoftirq(second.getSoftirq() - first.getSoftirq())
        .setSteal(second.getSteal() - first.getSteal())
        .setGuest(second.getGuest() - first.getGuest())
        .setGuestNice(second.getGuestNice() - first.getGuestNice())
        .build();
  }

  /** Take the forward difference of the samples' jiffies by cpu. */
  public static CpuDifference difference(CpuSample first, CpuSample second) {
    CpuDifference.Builder diff =
        CpuDifference.newBuilder().setStart(first.getTimestamp()).setEnd(second.getTimestamp());

    // TODO(timur): this looks awkward
    Map<Integer, CpuReading> firstMap =
        first.getReadingList().stream().collect(toMap(CpuReading::getCpu, r -> r));
    second.getReadingList().stream()
        // if this fails, the sample is bad
        .forEach(reading -> diff.addReading(difference(firstMap.get(reading.getCpu()), reading)));
    return diff.build();
  }

  /** Reads the system's stat file and returns individual cpus. */
  private static String[] readCpus() {
    String[] stats = new String[CPU_COUNT];
    try {
      BufferedReader reader = new BufferedReader(new FileReader(SYSTEM_STAT_FILE));
      reader.readLine(); // first line is total summary; we need by cpu
      for (int i = 0; i < CPU_COUNT; i++) {
        stats[i] = reader.readLine();
      }
      reader.close();
    } catch (Exception e) {
      logger.info("unable to read " + SYSTEM_STAT_FILE);
    }
    return stats;
  }

  /** Turns stat strings into a {@link eflect.sample.CpuSample}. */
  private static CpuSample.Builder parseCpus(String[] stats) {
    CpuSample.Builder sample = CpuSample.newBuilder();
    for (String statString : stats) {
      String[] stat = statString.split(" ");
      if (stat.length != 11) {
        continue;
      }
      sample.addReading(
          CpuReading.newBuilder()
              .setCpu(Integer.parseInt(stat[CpuIndex.CPU.index].substring(3)))
              .setUser(Integer.parseInt(stat[CpuIndex.USER.index]))
              .setNice(Integer.parseInt(stat[CpuIndex.NICE.index]))
              .setSystem(Integer.parseInt(stat[CpuIndex.SYSTEM.index]))
              // TODO: idle requires a long
              // .setIdle(Integer.parseInt(stat[CpuIndex.IDLE.index]))
              .setIowait(Integer.parseInt(stat[CpuIndex.IOWAIT.index]))
              .setIrq(Integer.parseInt(stat[CpuIndex.IRQ.index]))
              .setSoftirq(Integer.parseInt(stat[CpuIndex.SOFTIRQ.index]))
              .setSteal(Integer.parseInt(stat[CpuIndex.STEAL.index]))
              .setGuest(Integer.parseInt(stat[CpuIndex.GUEST.index]))
              .setGuestNice(Integer.parseInt(stat[CpuIndex.GUEST_NICE.index])));
    }
    return sample;
  }

  private CpuJiffies() {}
}
