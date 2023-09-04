package green.strategy;

import static java.util.stream.Collectors.joining;

import green.Knob;
import green.Strategy;
import green.knob.Knobs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** A {@link Strategy} that randomly selects a configuration and archives all evaluations. */
public final class RandomArchivingStrategy implements Strategy {
  private final HashMap<String, Knob> baseKnobs = new HashMap<>();
  private final ArrayList<Evaluation> data = new ArrayList<>();

  public RandomArchivingStrategy(Map<String, Knob> knobs) {
    knobs.forEach(this.baseKnobs::put);
  }

  /** Creates knobs with random values. */
  @Override
  public final Map<String, Knob> nextConfiguration() {
    return Knobs.withRandomValues(baseKnobs);
  }

  /** Stores each update pair in an underlying archive. */
  @Override
  public void update(Map<String, Knob> knobs, Map<String, Double> measurement) {
    data.add(new Evaluation(knobs, measurement));
  }

  /** Writes the data as a json list. */
  @Override
  public String toString() {
    return String.format(
        "{\"strategy\":\"random_archiving\",\"data\":[%s]}",
        data.stream().map(Evaluation::toString).collect(joining(",")));
  }

  /** Returns a shallow copy of all evaluations. */
  public ArrayList<Evaluation> getData() {
    return new ArrayList<>(data);
  }
}
