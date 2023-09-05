package eflect;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/** Helper to handle task jiffies from /proc/<pid>/task/<tid>/stat. */
public final class TaskJiffies {
  private static final Logger logger = LoggerUtil.getLogger();

  // task stat indicies
  private static final int STAT_LENGTH = 52;

  private enum TaskIndex {
    TID(0),
    CPU(38),
    USER(13),
    SYSTEM(14);

    private int index;

    private TaskIndex(int index) {
      this.index = index;
    }
  };

  private static final long PID = ProcessHandle.current().pid();

  /** Reads a process's tasks as a {@link eflect.TaskSample}. */
  public static TaskSample sample(long pid) {
    return parseTasks(readTasks(pid)).setTimestamp(Instant.now().toEpochMilli()).build();
  }

  /** Reads this process's tasks as a {@link eflect.TaskSample}. */
  static TaskSample sampleSelf() {
    return sample(PID);
  }

  /**
   * Take the difference of two readings. Assumes the task is the same but the first reading's cpu
   * is used.
   */
  public static TaskReading difference(TaskReading first, TaskReading second) {
    return TaskReading.newBuilder()
        .setTaskId(first.getTaskId())
        .setCpu(first.getCpu())
        .setName(first.getName())
        .setUser(second.getUser() - first.getUser())
        .setSystem(second.getSystem() - first.getSystem())
        .build();
  }

  /** Take the forward difference of the samples' jiffies by task. */
  public static TaskDifference difference(TaskSample first, TaskSample second) {
    TaskDifference.Builder diff =
        TaskDifference.newBuilder().setStart(first.getTimestamp()).setEnd(second.getTimestamp());

    Map<Integer, TaskReading> firstMap =
        first.getReadingList().stream().collect(toMap(TaskReading::getTaskId, r -> r));
    second.getReadingList().stream()
        .forEach(
            reading -> {
              if (firstMap.containsKey(reading.getTaskId())) {
                diff.addReading(difference(firstMap.get(reading.getTaskId()), reading));
              }
            });
    return diff.build();
  }

  /** Sort the samples by timestamp and compute the forward difference between pairs. */
  public static List<TaskDifference> difference(List<TaskSample> samples) {
    ArrayList<TaskDifference> diffs = new ArrayList<>();
    Optional<TaskSample> last = Optional.empty();
    for (TaskSample sample :
        samples.stream()
            .sorted((s1, s2) -> Long.compare(s1.getTimestamp(), s2.getTimestamp()))
            .collect(toList())) {
      if (last.isPresent()) {
        diffs.add(difference(last.get(), sample));
      }
      last = Optional.of(sample);
    }
    return diffs;
  }

  /** Reads stat files of tasks directory of a process. */
  private static final ArrayList<String> readTasks(long pid) {
    ArrayList<String> stats = new ArrayList<String>();
    File tasks = new File(String.join(File.separator, "/proc", Long.toString(pid), "task"));
    if (!tasks.exists()) {
      return stats;
    }

    for (File task : tasks.listFiles()) {
      File statFile = new File(task, "stat");
      if (!statFile.exists()) {
        continue;
      }
      try {
        BufferedReader reader = new BufferedReader(new FileReader(statFile));
        stats.add(reader.readLine());
        reader.close();
      } catch (Exception e) {
        logger.info("unable to read task " + statFile + " before it terminated");
      }
    }
    return stats;
  }

  /** Turns task stat strings into a {@link eflect.TaskSample}. */
  private static TaskSample.Builder parseTasks(ArrayList<String> stats) {
    TaskSample.Builder sample = TaskSample.newBuilder();
    stats.forEach(
        statString -> {
          String[] stat = statString.split(" ");
          if (stat.length >= STAT_LENGTH) {
            // task name can be space-delimited, so there may be extra entries
            int offset = stat.length - STAT_LENGTH;
            sample.addReading(
                TaskReading.newBuilder()
                    .setTaskId(Integer.parseInt(stat[TaskIndex.TID.index]))
                    // .setName(getName(stat, offset))
                    .setCpu(Integer.parseInt(stat[TaskIndex.CPU.index + offset]))
                    .setUser(Integer.parseInt(stat[TaskIndex.USER.index + offset]))
                    .setSystem(Integer.parseInt(stat[TaskIndex.SYSTEM.index + offset])));
          }
        });
    return sample;
  }

  /** Extracts the name from the stat string. */
  private static final String getName(String[] stat, int offset) {
    String name = String.join(" ", Arrays.copyOfRange(stat, 1, 2 + offset));
    return name.substring(1, name.length() - 1);
  }
}
