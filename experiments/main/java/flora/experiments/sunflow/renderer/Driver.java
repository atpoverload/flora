package flora.experiments.sunflow.renderer;

import flora.Machine;
import flora.Meter;
import flora.experiments.sunflow.ConfigurableScene;
import flora.experiments.sunflow.RenderingConfiguration;
import flora.experiments.sunflow.RenderingKnobs;
import flora.experiments.sunflow.image.BufferedImageDisplay;
import flora.experiments.sunflow.image.ImageDistanceMeter;
import flora.experiments.sunflow.image.ImageDistanceScore;
import flora.experiments.sunflow.scenes.CornellBox;
import flora.knob.meta.RangeConstrainedKnob;
import flora.meter.contrib.EflectMeter;
import flora.strategy.contrib.ears.CompatNumberProblem;
import flora.strategy.contrib.ears.RawWorkFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.sunflow.system.UI;
import org.um.feri.ears.algorithms.moo.nsga2.D_NSGAII;
import org.um.feri.ears.problems.StopCriterion;
import org.um.feri.ears.problems.Task;

final class Driver {
  private static final int REFERENCE_TIMEOUT =
      60 * 60 * 1000; // 1 hour timeout for the reference image

  private static class RawSceneFactory
      implements RawWorkFactory<RangeConstrainedKnob, ConfigurableScene> {
    private final ConfigurableScene scene;
    private final RangeConstrainedKnob[] knobs;

    private RawSceneFactory(ConfigurableScene scene) {
      this.scene = scene;
      this.knobs =
          Arrays.stream(scene.knobs().asArray())
              .map(RangeConstrainedKnob::new)
              .toArray(RangeConstrainedKnob[]::new);
    }

    @Override
    public RangeConstrainedKnob[] knobs() {
      return this.knobs;
    }

    @Override
    public ConfigurableScene fromIndices(int[] configuration) {
      return scene.newScene(scene.knobs(), scene.knobs().fromIndices(configuration));
    }
  }

  public static void main(String[] args) throws Exception {
    // Setup the knobs and display
    UI.verbosity(0);
    BufferedImageDisplay display = new BufferedImageDisplay();
    RenderingKnobs knobs = RenderingKnobs.DEFAULT;
    RangeConstrainedKnob[] knobsArray =
        Arrays.stream(knobs.asArray())
            .map(RangeConstrainedKnob::new)
            .toArray(RangeConstrainedKnob[]::new);

    // generate the reference
    CornellBox scene =
        new CornellBox(knobs, RenderingConfiguration.DEFAULT, display, REFERENCE_TIMEOUT);
    Instant start = Instant.now();
    scene.run();
    int timeOut = (int) (3 * Duration.between(start, Instant.now()).toMillis() / 2);
    System.out.println("TIMEOUT IS " + timeOut);

    // setup the meters with the reference
    Map<String, Meter> meters =
        Map.of(
            "energy",
            EflectMeter.newLocalMeter(4),
            "score",
            new ImageDistanceMeter(display, display.getImage(), ImageDistanceScore.MSE));

    // wire everything together
    Machine machine =
        new Machine() {
          @Override
          public Map<String, Meter> meters() {
            return new HashMap<>(meters);
          }
        };

    CompatNumberProblem<RangeConstrainedKnob> problem =
        new CompatNumberProblem<>(
            "rendering-problem",
            new RawSceneFactory(
                new CornellBox(knobs, RenderingConfiguration.DEFAULT, display, timeOut)),
            machine);

    // run the renderer
    D_NSGAII nsga = new D_NSGAII();
    nsga.execute(new Task<>(problem, StopCriterion.EVALUATIONS, 100000, 0, 0));
  }
}
