package flora.experiments.sunflow.renderers;

import flora.experiments.sunflow.scene.Filter;
import flora.experiments.sunflow.scene.RenderingConfiguration;
import flora.experiments.sunflow.scene.RenderingKnobs;
import flora.experiments.sunflow.scene.util.JsonSceneUtil;
import flora.knob.EnumKnob;
import flora.knob.IntRangeKnob;
import flora.knob.util.Knobs;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.OptionalDouble;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class RenderingArgs {
  // Defaults for rendering.
  private static final int MIN_SIZE = 128;
  private static final int MAX_SIZE = 640;
  private static final RenderingKnobs DEFAULT_KNOBS =
      new RenderingKnobs(
          /* threads= */ Knobs.ALL_CPUS,
          /* width= */ new IntRangeKnob(MIN_SIZE, MAX_SIZE),
          /* height= */ new IntRangeKnob(MIN_SIZE, MAX_SIZE),
          /* aaMin= */ new IntRangeKnob(-4, 4),
          /* aaMax= */ new IntRangeKnob(-2, 4),
          /* bucketSize= */ new IntRangeKnob(16, 512),
          /* aoSamples= */ new IntRangeKnob(1, 64),
          new EnumKnob<>(Filter.class));

  private static final int AA_MIN = 1;
  private static final int AA_MAX = 2;
  private static final int BUCKET_SIZE = 32;
  private static final int AO_SAMPLES = 64;
  private static final RenderingConfiguration REFERENCE_CONFIGURATION =
      new RenderingConfiguration(
          1,
          DEFAULT_KNOBS.width.end(),
          DEFAULT_KNOBS.height.end(),
          AA_MIN,
          AA_MAX,
          BUCKET_SIZE,
          AO_SAMPLES,
          Filter.BLACKMAN_HARRIS);

  private static final Options OPTIONS =
      new Options()
          .addOption("s", "scene", true, "scene to render")
          .addOption("k", "knobs", true, "rendering knobs to use")
          .addOption("o", "output", true, "file to output to")
          .addOption("c", "configuration", true, "reference configuration to use")
          .addOption("r", "reference", false, "use the default reference configuration")
          .addOption("t", "constraint", true, "maximum allowable reference scoring");

  static RenderingArgs fromArgs(String[] args) throws Exception {
    CommandLine cmd = new DefaultParser().parse(OPTIONS, args);
    Optional<String> sceneFile =
        cmd.hasOption("scene") ? Optional.of(cmd.getOptionValue("scene")) : Optional.empty();
    RenderingKnobs knobs =
        cmd.hasOption("knobs")
            ? JsonSceneUtil.parseKnobs(Files.readString(Paths.get(cmd.getOptionValue("knobs"))))
            : DEFAULT_KNOBS;
    Optional<RenderingConfiguration> configuration = Optional.empty();
    if (cmd.hasOption("reference") && cmd.hasOption("configuration")) {
      throw new IllegalArgumentException(
          "Only one of \"configuration\" or \"reference\" can be present.");
    } else if (cmd.hasOption("reference")) {
      configuration = Optional.of(REFERENCE_CONFIGURATION);
    } else if (cmd.hasOption("configuration")) {
      configuration =
          Optional.of(
              JsonSceneUtil.parseConfiguration(
                  Files.readString(Paths.get(cmd.getOptionValue("configuration")))));
    }
    OptionalDouble constraint =
        cmd.hasOption("constraint")
            ? OptionalDouble.of(Double.parseDouble(cmd.getOptionValue("constraint")))
            : OptionalDouble.empty();
    return new RenderingArgs(new RenderingEngine(sceneFile, knobs, configuration, constraint), cmd);
  }

  final RenderingEngine engine;
  final CommandLine cmd;

  RenderingArgs(RenderingEngine engine, CommandLine cmd) {
    this.engine = engine;
    this.cmd = cmd;
  }
}
