package eflect;

import static eflect.VirtualizationUtil.forwardDifference;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** Static class that tries to virtualize task activity given cpu and task jiffies. */
public class JiffiesVirtualizer {
  private static final Logger logger = LoggerUtil.getLogger();

  /** Tries to align the {@link TaskSamples} and {@link CpuSamples} into virtual activity. */
  public static List<Virtualization> virtualize(
      List<CpuSample> cpus, List<TaskSample> tasks, long millisThresh) {
    if (cpus.size() < 2 || tasks.size() < 2) {
      logger.info(
          String.format(
              "not enough samples to align data (cpu: %d, tasks: %d)", cpus.size(), tasks.size()));
      return new ArrayList<>();
    }

    return align(
        forwardDifference(
            cpus.stream()
                .sorted((s1, s2) -> Long.compare(s1.getTimestamp(), s2.getTimestamp()))
                .collect(toList()),
            CpuJiffies::difference),
        forwardDifference(
            tasks.stream()
                .sorted((s1, s2) -> Long.compare(s1.getTimestamp(), s2.getTimestamp()))
                .collect(toList()),
            TaskJiffies::difference),
        millisThresh);
  }

  private static List<Virtualization> align(
      List<CpuDifference> cpus, List<TaskDifference> tasks, long millisThresh) {
    Map<Long, List<CpuDifference>> cpuMap =
        cpus.stream()
            .collect(groupingBy(diff -> diff.getStart() / millisThresh));
    Map<Long, List<TaskDifference>> taskMap =
        tasks.stream()
            .collect(groupingBy(diff -> diff.getStart() / millisThresh));

    HashSet<Long> buckets = new HashSet<>(taskMap.keySet());
    buckets.retainAll(cpuMap.keySet());
    return buckets.stream()
        .map(
            bucket ->
                Virtualization.newBuilder()
                    .setStart(millisThresh * bucket)
                    .setEnd(millisThresh * (bucket + 1))
                    .addAllVirtualization(
                        virtualizeJiffies(cpuMap.get(bucket), taskMap.get(bucket)))
                    .build())
        .collect(toList());
  }

  private static List<Virtualization.VirtualizedComponent> virtualizeJiffies(
      List<CpuDifference> cpus, List<TaskDifference> tasks) {
    int[] cpuJiffies = getJiffiesByCpu(cpus);
    int[] taskJiffies = getTaskJiffiesByCpu(tasks);
    return getJiffiesByTask(tasks).stream()
        .map(
            r ->
                virtualizeTask(
                    r, Math.max(Math.max(cpuJiffies[r.getCpu()], taskJiffies[r.getCpu()]), 1)))
        .collect(toList());
  }

  private static int[] getJiffiesByCpu(List<CpuDifference> cpus) {
    return cpus.stream()
        .flatMap(diff -> diff.getReadingList().stream())
        .collect(groupingBy(r -> r.getCpu()))
        .entrySet()
        .stream()
        .sorted(comparing(e -> e.getKey()))
        .mapToInt(
            e ->
                e.getValue().stream()
                    .mapToInt(
                        r ->
                            r.getUser()
                                + r.getNice()
                                + r.getSystem()
                                + r.getIowait()
                                + r.getIrq()
                                + r.getSoftirq()
                                + r.getSteal()
                                + r.getGuest()
                                + r.getGuestNice())
                    .sum())
        .toArray();
  }

  private static int[] getTaskJiffiesByCpu(List<TaskDifference> tasks) {
    return tasks.stream()
        .flatMap(diff -> diff.getReadingList().stream())
        .collect(groupingBy(r -> r.getCpu()))
        .entrySet()
        .stream()
        .sorted(comparing(e -> e.getKey()))
        .mapToInt(e -> e.getValue().stream().mapToInt(r -> r.getUser() + r.getSystem()).sum())
        .toArray();
  }

  private static List<TaskReading> getJiffiesByTask(List<TaskDifference> tasks) {
    return tasks.stream()
        .flatMap(diff -> diff.getReadingList().stream())
        .collect(groupingBy(r -> r.getTaskId()))
        .entrySet()
        .stream()
        .map(
            e -> {
              TaskReading.Builder builder =
                  TaskReading.newBuilder()
                      .setTaskId(e.getKey())
                      .setName(e.getValue().get(0).getName())
                      .setCpu(e.getValue().get(0).getCpu());
              for (TaskReading reading : e.getValue()) {
                builder
                    .setUser(builder.getUser() + reading.getUser())
                    .setSystem(builder.getSystem() + reading.getSystem());
              }
              return builder.build();
            })
        .collect(toList());
  }

  private static Virtualization.VirtualizedComponent virtualizeTask(
      TaskReading task, int totalJiffies) {
    return Virtualization.VirtualizedComponent.newBuilder()
        .setTaskId(task.getTaskId())
        .setComponent(
            Virtualization.VirtualizedComponent.Component.newBuilder().setCpu(task.getCpu()))
        .setUnit(Virtualization.VirtualizedComponent.Unit.ACTIVITY)
        .setValue((double) (task.getUser() + task.getSystem()) / ((double) (totalJiffies)))
        .build();
  }

  private JiffiesVirtualizer() {}
}
