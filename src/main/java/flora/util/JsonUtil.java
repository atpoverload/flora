package flora.util;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.function.Function;
import org.json.JSONObject;

public final class JsonUtil {
  public static <C> JSONObject toJson(
      DataCollector<?, C> collector, Function<C, JSONObject> transformer) {
    HashMap<String, Object> data = new HashMap<>();
    if (!collector.hasConfigurations()) {
      data.put(
          "configurations",
          collector.getConfigurations().entrySet().stream()
              .collect(toMap(e -> e.getKey(), e -> transformer.apply(e.getValue()))));
    }
    if (!collector.hasMeasurements()) {
      data.put("measurements", collector.getMeasurements());
    }
    if (!collector.hasErrors()) {
      data.put(
          "errors",
          collector.getErrors().entrySet().stream()
              .collect(toMap(e -> e.getKey(), e -> e.getValue().description())));
    }
    return new JSONObject(data);
  }
}
