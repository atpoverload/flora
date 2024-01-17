package flora.experiments.sunflow.scene.util;

import static org.junit.Assert.assertEquals;

import flora.experiments.sunflow.scene.Filter;
import flora.experiments.sunflow.scene.RenderingConfiguration;
import flora.experiments.sunflow.scene.RenderingKnobs;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JsonSceneUtilTest {
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

  @Test
  public void parseKnobs_jsonFile_success() throws Exception {
    RenderingKnobs knobs =
        JsonSceneUtil.parseKnobs(
            new JSONObject(
                Files.readString(
                    Paths.get(
                        "experiments/test/java/flora/experiments/sunflow/scene/util/test_knobs.json"))));

    assertEquals(CPU_COUNT, knobs.threads.configurationCount());
    assertEquals(1, knobs.threads.start());
    assertEquals(CPU_COUNT, knobs.threads.end());
    assertEquals(1, knobs.threads.step());

    assertEquals(13, knobs.width.configurationCount());
    assertEquals(4, knobs.width.start());
    assertEquals(16, knobs.width.end());
    assertEquals(1, knobs.width.step());

    assertEquals(13, knobs.height.configurationCount());
    assertEquals(4, knobs.height.start());
    assertEquals(16, knobs.height.end());
    assertEquals(1, knobs.height.step());

    assertEquals(4, knobs.aaMin.configurationCount());
    assertEquals(-1, knobs.aaMin.start());
    assertEquals(2, knobs.aaMin.end());
    assertEquals(1, knobs.aaMin.step());

    assertEquals(4, knobs.aaMax.configurationCount());
    assertEquals(-1, knobs.aaMax.start());
    assertEquals(2, knobs.aaMax.end());
    assertEquals(1, knobs.aaMax.step());

    assertEquals(49, knobs.bucketSize.configurationCount());
    assertEquals(16, knobs.bucketSize.start());
    assertEquals(64, knobs.bucketSize.end());
    assertEquals(1, knobs.bucketSize.step());

    assertEquals(16, knobs.aoSamples.configurationCount());
    assertEquals(1, knobs.aoSamples.start());
    assertEquals(16, knobs.aoSamples.end());
    assertEquals(1, knobs.aoSamples.step());

    assertEquals(Filter.values().length, knobs.filter.configurationCount());
  }

  @Test
  public void parseConfiguration_jsonFile_success() throws Exception {
    RenderingConfiguration configuration =
        JsonSceneUtil.parseConfiguration(
            new JSONObject(
                Files.readString(
                    Paths.get(
                        "experiments/test/java/flora/experiments/sunflow/scene/util/test_configuration.json"))));

    assertEquals(1, configuration.threads());
    assertEquals(64, configuration.width());
    assertEquals(64, configuration.height());
    assertEquals(1, configuration.aaMin());
    assertEquals(2, configuration.aaMax());
    assertEquals(32, configuration.bucketSize());
    assertEquals(16, configuration.aoSamples());
    assertEquals(Filter.TRIANGLE, configuration.filter());
  }
}
