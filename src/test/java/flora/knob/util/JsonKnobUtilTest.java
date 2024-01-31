package flora.knob.util;

import static org.junit.Assert.assertEquals;

import flora.knob.IntCollectionKnob;
import flora.knob.IntRangeKnob;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JsonKnobUtilTest {
  @Test
  public void parseIntRangeKnob_jsonString1_success() {
    IntRangeKnob knob =
        JsonKnobUtil.parseIntRangeKnob(
            new JSONObject("{\"type\": \"IntRangeKnob\",\"start\": 1,\"end\": 2}"));

    assertEquals(2, knob.configurationCount());
    assertEquals(1, knob.start());
    assertEquals(2, knob.end());
    assertEquals(1, knob.step());
  }

  @Test
  public void parseIntRangeKnob_jsonString2_success() {
    IntRangeKnob knob =
        JsonKnobUtil.parseIntRangeKnob(
            new JSONObject("{\"type\": \"IntRangeKnob\",\"start\": 1,\"end\": 2, \"step\": 2}"));

    assertEquals(1, knob.configurationCount());
    assertEquals(1, knob.start());
    assertEquals(2, knob.end());
    assertEquals(2, knob.step());
  }

  @Test
  public void parseIntCollectionKnob_jsonString_success() {
    IntCollectionKnob knob =
        JsonKnobUtil.parseIntCollectionKnob(
            new JSONObject("{\"type\": \"IntRangeKnob\",\"values\": [1, 2]"));

    assertEquals(2, knob.configurationCount());
    assertEquals(1, knob.values()[0]);
    assertEquals(2, knob.values()[1]);
  }
}
