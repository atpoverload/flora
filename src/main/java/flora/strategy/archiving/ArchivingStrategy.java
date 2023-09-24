package flora.strategy.archiving;

import static java.util.stream.Collectors.joining;

import flora.Strategy;
import flora.WorkloadContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** A {@link Strategy} that archives all updates. Users must extend {@link context}. */
public abstract class ArchivingStrategy<K, C, Ctx extends WorkloadContext<K, C>>
    implements Strategy<K, C, Ctx> {
  private final List<ArchiveRecord<K, C, Ctx>> records = new ArrayList<>();

  /** Stores each update pair in an underlying archive. */
  @Override
  public void update(Ctx context, Map<String, Double> measurement) {
    records.add(new ArchiveRecord<>(context, measurement));
  }

  /** Returns the records as a json list. */
  @Override
  public String toString() {
    return String.format(
        "[%s]", records.stream().map(ArchiveRecord::toString).collect(joining(",")));
  }

  /** Returns a shallow copy of all updates. */
  public List<ArchiveRecord<K, C, Ctx>> records() {
    return new ArrayList<>(records);
  }

  /** Returns a summary of updates by unique configuration. */
  public ArchiveRecordSummary<C> summary() {
    return new ArchiveRecordSummary<C>(records);
  }

  protected ArchivingStrategy() {}
}
