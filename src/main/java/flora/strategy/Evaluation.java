package flora.strategy;

import static java.util.stream.Collectors.joining;

import flora.Knob;
import flora.knob.Knobs;
import java.util.Map;

public class Evaluation {
  private final Map<String, Knob> knobs;
  private final Map<String, Double> measurement;

  public Evaluation(Map<String, Knob> knobs, Map<String, Double> measurement) {
    this.knobs = knobs;
    this.measurement = measurement;
  }

  /** Writes the knobs and measurements as a json dict of dicts. */
  @Override
  public String toString() {
    return String.format(
        "{\"knobs\":%s,\"measurements\":%s}",
        Knobs.valuesToJson(knobs),
        String.format(
            "{%s}",
            measurement.entrySet().stream()
                .map(e -> String.format("\"%s\":%f", e.getKey(), e.getValue()))
                .collect(joining(","))));
  }
}
