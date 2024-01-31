package flora.experiments.sunflow.scene.util;

import static flora.knob.util.JsonKnobUtil.parseIntRangeKnob;
import static flora.knob.util.Knobs.ALL_CPUS;

import flora.experiments.sunflow.scene.Filter;
import flora.experiments.sunflow.scene.RenderingConfiguration;
import flora.experiments.sunflow.scene.RenderingKnobs;
import flora.knob.EnumKnob;
import flora.knob.util.JsonKnobUtil;
import org.json.JSONObject;

public final class JsonSceneUtil {
  public static JSONObject toJson(RenderingConfiguration configuration) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("threads", configuration.threads());
    jsonObject.put("width", configuration.width());
    jsonObject.put("height", configuration.height());
    jsonObject.put("aaMin", configuration.aaMin());
    jsonObject.put("aaMax", configuration.aaMax());
    jsonObject.put("bucketSize", configuration.bucketSize());
    jsonObject.put("aoSamples", configuration.aoSamples());
    jsonObject.put("filter", configuration.filter().toString());
    return jsonObject;
  }

  public static JSONObject toJson(RenderingKnobs knobs) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("threads", JsonKnobUtil.toJson(knobs.threads));
    jsonObject.put("width", JsonKnobUtil.toJson(knobs.width));
    jsonObject.put("height", JsonKnobUtil.toJson(knobs.height));
    jsonObject.put("aaMin", JsonKnobUtil.toJson(knobs.aaMin));
    jsonObject.put("aaMax", JsonKnobUtil.toJson(knobs.aaMax));
    jsonObject.put("bucketSize", JsonKnobUtil.toJson(knobs.bucketSize));
    jsonObject.put("aoSamples", JsonKnobUtil.toJson(knobs.aoSamples));
    jsonObject.put("filter", JsonKnobUtil.toJson(knobs.filter));
    return jsonObject;
  }

  public static RenderingConfiguration parseConfiguration(String jsonObject) {
    return parseConfiguration(new JSONObject(jsonObject));
  }

  public static RenderingConfiguration parseConfiguration(JSONObject jsonObject) {
    return new RenderingConfiguration(
        jsonObject.getInt("threads"),
        jsonObject.getInt("width"),
        jsonObject.getInt("height"),
        jsonObject.getInt("aaMin"),
        jsonObject.getInt("aaMax"),
        jsonObject.getInt("bucketSize"),
        jsonObject.getInt("aoSamples"),
        Filter.valueOf(jsonObject.getString("filter")));
  }

  public static RenderingKnobs parseKnobs(String jsonObject) {
    return parseKnobs(new JSONObject(jsonObject));
  }

  public static RenderingKnobs parseKnobs(JSONObject jsonObject) {
    return new RenderingKnobs(
        ALL_CPUS,
        parseIntRangeKnob(jsonObject.getJSONObject("width")),
        parseIntRangeKnob(jsonObject.getJSONObject("height")),
        parseIntRangeKnob(jsonObject.getJSONObject("aaMin")),
        parseIntRangeKnob(jsonObject.getJSONObject("aaMax")),
        parseIntRangeKnob(jsonObject.getJSONObject("bucketSize")),
        parseIntRangeKnob(jsonObject.getJSONObject("aoSamples")),
        new EnumKnob<>(Filter.class));
  }

  private JsonSceneUtil() {}
}
