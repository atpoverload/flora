package flora.strategy;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.summingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import flora.KnobContext;
import flora.Strategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A {@link Strategy} that archives all updates. Users can extend {@link context}. */
public abstract class ArchivingStrategy<K, C, KC extends KnobContext<K, C>>
    implements Strategy<K, C, KC> {
  public static final class ArchiveRecord<K, C, KC extends KnobContext<K, C>> {
    private final KC context;
    private final Map<String, Double> measurement;

    private ArchiveRecord(KC context, Map<String, Double> measurement) {
      this.context = context;
      this.measurement = measurement;
    }

    public KC context() {
      return context;
    }

    public Map<String, Double> measurement() {
      return new HashMap<>(measurement);
    }

    /** Returns the record contents as a json dict. */
    @Override
    public String toString() {
      return String.format(
          "{\"context\":%s,\"measurement\":{%s}}",
          context,
          measurement.entrySet().stream()
              .map(e -> String.format("\"%s\":%f", e.getKey(), e.getValue()))
              .collect(joining(",")));
    }
  }

  public static final class ArchiveRecordSummary<K, C, KC extends KnobContext<K, C>> {
    private final Map<C, Map<String, Long>> counts;
    private final Map<C, Map<String, Double>> means;
    private final Map<C, Map<String, Double>> deviations;

    private ArchiveRecordSummary(List<ArchiveRecord<K, C, KC>> data) {
      Map<C, List<ArchiveRecord<K, C, KC>>> groupedData =
          data.stream().collect(groupingBy(record -> record.context().configuration(), toList()));
      counts =
          groupedData.entrySet().stream()
              .collect(
                  toMap(
                      e -> e.getKey(),
                      e ->
                          e.getValue().stream()
                              .flatMap(record -> record.measurement().entrySet().stream())
                              .collect(groupingBy(e1 -> e1.getKey(), counting()))));
      means =
          groupedData.entrySet().stream()
              .collect(
                  toMap(
                      e -> e.getKey(),
                      e ->
                          e.getValue().stream()
                              .flatMap(record -> record.measurement().entrySet().stream())
                              .collect(
                                  groupingBy(
                                      e1 -> e1.getKey(), averagingDouble(e1 -> e1.getValue())))));
      deviations =
          groupedData.entrySet().stream()
              .collect(
                  toMap(
                      e -> e.getKey(),
                      e ->
                          e.getValue().stream()
                              .flatMap(record -> record.measurement().entrySet().stream())
                              .collect(
                                  groupingBy(
                                      e1 -> e1.getKey(),
                                      summingDouble(
                                          e1 ->
                                              Math.pow(
                                                  e1.getValue()
                                                      - means.get(e.getKey()).get(e1.getKey()),
                                                  2))))
                              .entrySet()
                              .stream()
                              .collect(
                                  toMap(
                                      e1 -> e1.getKey(),
                                      e1 ->
                                          Math.sqrt(
                                              e1.getValue()
                                                  / counts.get(e.getKey()).get(e1.getKey()))))));
    }

    public Map<C, Map<String, Long>> counts() {
      return new HashMap<>(counts);
    }

    public Map<C, Map<String, Double>> means() {
      return new HashMap<>(means);
    }

    public Map<C, Map<String, Double>> deviations() {
      return new HashMap<>(deviations);
    }
  }

  private final ArrayList<ArchiveRecord<K, C, KC>> data = new ArrayList<>();

  /** Stores each update pair in an underlying archive. */
  @Override
  public void update(KC context, Map<String, Double> measurement) {
    data.add(new ArchiveRecord<>(context, measurement));
  }

  /** Returns the data as a json. It is recommended to override the context's {@link toString}. */
  @Override
  public String toString() {
    return String.format(
        "[%s]",
        data.stream()
            .map(
                record ->
                    String.format(
                        "{\"context\":%s,\"measurement\":[%s]}",
                        record.context,
                        record.measurement.entrySet().stream()
                            .map(e -> String.format("\"%s\"=%f", e.getKey(), e.getValue()))
                            .collect(joining(","))))
            .collect(joining(",")));
  }

  /** Returns a shallow copy of all updates. */
  public List<ArchiveRecord<K, C, KC>> data() {
    return new ArrayList<>(data);
  }

  /** Returns a summary of updates by unique configuration. */
  public ArchiveRecordSummary<K, C, KC> summary() {
    return new ArchiveRecordSummary<K, C, KC>(data);
  }

  protected ArchivingStrategy() {}
}
