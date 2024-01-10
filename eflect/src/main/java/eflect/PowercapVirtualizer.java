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

/** Static class that tries to virtualize rapl energy given application activity. */
public class PowercapVirtualizer {
  private static final Logger logger = LoggerUtil.getLogger();

  /**
   * Tries to align the {@link PowercapSample} and activity {@link Virtualizations} into energy
   * virtualizations.
   */
  public static List<Virtualization> virtualize(
      List<PowercapSample> powercap, List<Virtualization> activity, long millisThresh) {
    if (powercap.size() < 2) {
      logger.fine(
          String.format("not enough samples to align data (powercap: %d)", powercap.size()));
      return new ArrayList<>();
    }

    if (activity.stream()
        .noneMatch(
            v ->
                v.getVirtualizationList().stream()
                    .anyMatch(PowercapVirtualizer::hasCpuComponent))) {
      logger.fine(String.format("no cpu activity virtualizations"));
      return new ArrayList<>();
    }

    // TODO(timur): our implementation uses powercap, so this won't work with rapl
    return align(
        forwardDifference(
            powercap.stream()
                .sorted((s1, s2) -> Long.compare(s1.getTimestamp(), s2.getTimestamp()))
                .collect(toList()),
            Powercap::difference),
        activity,
        millisThresh);
  }

  private static List<Virtualization> align(
      List<PowercapDifference> rapl, List<Virtualization> activity, long millisThresh) {
    Map<Long, List<PowercapDifference>> raplMap =
        rapl.stream().collect(groupingBy(diff -> diff.getStart() / millisThresh));
    Map<Long, List<Virtualization>> activityMap =
        activity.stream().collect(groupingBy(v -> v.getStart() / millisThresh));

    HashSet<Long> buckets = new HashSet<>(activityMap.keySet());
    buckets.retainAll(raplMap.keySet());
    return buckets.stream()
        .map(
            bucket ->
                Virtualization.newBuilder()
                    .setStart(millisThresh * bucket)
                    .setEnd(millisThresh * (bucket + 1))
                    .addAllVirtualization(
                        virtualizeEnergy(raplMap.get(bucket), activityMap.get(bucket)))
                    .build())
        .collect(toList());
  }

  private static List<Virtualization.VirtualizedComponent> virtualizeEnergy(
      List<PowercapDifference> rapl, List<Virtualization> activity) {
    double[] energy = getEnergyBySocket(rapl);
    return getActivityByTask(activity).stream()
        .map(
            c ->
                c.toBuilder()
                    .setUnit(Virtualization.VirtualizedComponent.Unit.ENERGY)
                    .setValue(c.getValue() * energy[c.getComponent().getCpu() / 20])
                    .build())
        .collect(toList());
  }

  private static double[] getEnergyBySocket(List<PowercapDifference> rapl) {
    return rapl.stream()
        .flatMap(diff -> diff.getConsumptionList().stream())
        .collect(groupingBy(r -> r.getSocket()))
        .entrySet()
        .stream()
        .sorted(comparing(e -> e.getKey()))
        .mapToDouble(
            e -> e.getValue().stream().mapToDouble(r -> r.getPackage() + r.getDram()).sum())
        .toArray();
  }

  private static List<Virtualization.VirtualizedComponent> getActivityByTask(
      List<Virtualization> activity) {
    return activity.stream()
        .flatMap(diff -> diff.getVirtualizationList().stream())
        .filter(PowercapVirtualizer::hasCpuComponent)
        .collect(groupingBy(r -> r.getTaskId()))
        .entrySet()
        .stream()
        .sorted(comparing(e -> e.getKey()))
        .map(
            e -> {
              Virtualization.VirtualizedComponent.Builder builder =
                  Virtualization.VirtualizedComponent.newBuilder()
                      .setTaskId(e.getKey())
                      .setComponent(e.getValue().get(0).getComponent())
                      .setUnit(Virtualization.VirtualizedComponent.Unit.ACTIVITY);
              for (Virtualization.VirtualizedComponent component : e.getValue()) {
                builder.setValue(builder.getValue() + component.getValue());
              }
              return builder.build();
            })
        .collect(toList());
  }

  private static boolean hasCpuComponent(Virtualization.VirtualizedComponent component) {
    return component.getComponent().getComponentCase()
        == Virtualization.VirtualizedComponent.Component.ComponentCase.CPU;
  }

  private PowercapVirtualizer() {}
}
