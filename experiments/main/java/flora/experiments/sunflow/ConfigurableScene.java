package flora.experiments.sunflow;

import flora.Knob;
import flora.knob.EnumKnob;
import flora.knob.IntRangeKnob;
import java.util.Map;
import org.sunflow.SunflowAPI;

/** An abstract class that can configure rendering settings for a scene. */
public abstract class ConfigurableScene extends SunflowAPI {
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

  /** The default knobs used for the reference image. */
  public static Map<String, Knob> getKnobs() {
    Map<String, Knob> knobs = createKnobs();
    ((IntRangeKnob) knobs.get("threads")).setValue(1);
    ((IntRangeKnob) knobs.get("resolutionX")).setValue(768);
    ((IntRangeKnob) knobs.get("resolutionY")).setValue(768);
    ((IntRangeKnob) knobs.get("aaMin")).setValue(1);
    ((IntRangeKnob) knobs.get("aaMax")).setValue(2);
    ((IntRangeKnob) knobs.get("bucketSize")).setValue(32);
    ((IntRangeKnob) knobs.get("aoSamples")).setValue(64);
    ((EnumKnob<Filter>) knobs.get("filter")).setValue("BLACKMAN_HARRIS");
    return knobs;
  }

  private static Map<String, Knob> createKnobs() {
    return Map.of(
        "threads",
        new IntRangeKnob(1, CPU_COUNT),
        "resolutionX",
        new IntRangeKnob(256, 768),
        "resolutionY",
        new IntRangeKnob(256, 768),
        "aaMin",
        new IntRangeKnob(-4, 5),
        "aaMax",
        new IntRangeKnob(-4, 5),
        "bucketSize",
        new IntRangeKnob(1, 128),
        "aoSamples",
        new IntRangeKnob(32, 128),
        "filter",
        new EnumKnob<>(Filter.TRIANGLE));
  }

  private final Map<String, Knob> knobs;

  protected ConfigurableScene(Map<String, Knob> knobs) {
    this.knobs = knobs;
  }

  /** Builds the configurable portion of the scene. */
  @Override
  public final void build() {
    parameter("threads", knobs.get("threads").getInt());
    // spawn regular priority threads
    parameter("threads.lowPriority", false);
    parameter("resolutionX", knobs.get("resolutionX").getInt());
    parameter("resolutionY", knobs.get("resolutionY").getInt());
    parameter("aa.min", knobs.get("aaMin").getInt());
    parameter("aa.max", knobs.get("aaMax").getInt());
    parameter("filter", Filter.fromKnob(knobs.get("filter")).toString());
    parameter("depths.diffuse", 2);
    parameter("depths.reflection", 2);
    parameter("depths.refraction", 2);
    parameter("bucket.order", "hilbert");
    parameter("bucket.size", knobs.get("bucketSize").getInt());

    parameter("gi.engine", "ambocc");
    parameter("gi.ambocc.samples", knobs.get("aoSamples").getInt());
    parameter("gi.ambocc.maxdist", 600.0f);

    buildScene();
  }

  /** Builds the scene that is to be rendered. */
  protected abstract void buildScene();
}
