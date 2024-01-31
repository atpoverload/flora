package flora.knob.util;

import flora.Knob;
import flora.knob.BooleanKnob;
import flora.knob.EnumKnob;
import flora.knob.IntCollectionKnob;
import flora.knob.IntRangeKnob;
import org.json.JSONObject;

public final class JsonKnobUtil {
  public static JSONObject toJson(Knob knob) {
    if (knob instanceof BooleanKnob) {
      return booleanKnob();
    } else if (knob instanceof EnumKnob) {
      EnumKnob<?> enumKnob = (EnumKnob) knob;
      return enumKnob(enumKnob.enumType());
    } else if (knob instanceof IntRangeKnob) {
      return toJson((IntRangeKnob) knob);
    } else if (knob instanceof IntCollectionKnob) {
      return toJson((IntCollectionKnob) knob);
    }
    throw new RuntimeException(String.format("unsupported knob %s", knob));
  }

  private static JSONObject booleanKnob() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("type", "BooleanKnob");
    return jsonObject;
  }

  private static JSONObject enumKnob(Class<?> enumClass) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("type", "EnumKnob");
    jsonObject.put("enum_type", enumClass.getSimpleName());
    return jsonObject;
  }

  private static JSONObject toJson(IntRangeKnob knob) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("type", "IntRangeKnob");
    jsonObject.put("start", knob.start());
    jsonObject.put("end", knob.end());
    jsonObject.put("step", knob.step());
    return jsonObject;
  }

  private static JSONObject toJson(IntCollectionKnob knob) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("type", "IntCollectionKnob");
    jsonObject.put("values", knob.values());
    return jsonObject;
  }

  public static IntRangeKnob parseIntRangeKnob(String jsonObject) {
    return parseIntRangeKnob(new JSONObject(jsonObject));
  }

  public static IntRangeKnob parseIntRangeKnob(JSONObject jsonObject) {
    return new IntRangeKnob(
        jsonObject.getInt("start"), jsonObject.getInt("end"), jsonObject.optInt("step", 1));
  }

  public static IntCollectionKnob parseIntCollectionKnob(String jsonObject) {
    return parseIntCollectionKnob(new JSONObject(jsonObject));
  }

  public static IntCollectionKnob parseIntCollectionKnob(JSONObject jsonObject) {
    int[] values = new int[jsonObject.getJSONArray("values").length()];
    for (int i = 0; i < values.length; i++) {
      values[i] = jsonObject.getJSONArray("values").getInt(i);
    }
    return new IntCollectionKnob(values);
  }

  private JsonKnobUtil() {}
}
