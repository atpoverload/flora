package flora.experiments.sunflow.renderer;

import flora.Machine;
import flora.Meter;
import flora.experiments.sunflow.ConfigurableScene;
import flora.experiments.sunflow.ConfigurableSceneFactory;
import flora.experiments.sunflow.RenderingConfiguration;
import flora.experiments.sunflow.RenderingKnobs;
import flora.experiments.sunflow.image.BufferedImageDisplay;
import flora.experiments.sunflow.image.ImageDistanceMeter;
import flora.experiments.sunflow.image.ImageDistanceScore;
import flora.experiments.sunflow.scenes.ConfigurableFileScene;
import flora.experiments.sunflow.scenes.CornellBox;
import flora.knob.meta.RangeConstrainedKnob;
import flora.meter.Stopwatch;
import flora.meter.contrib.EflectMeter;
import flora.strategy.contrib.ears.CompatNumberProblem;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.sunflow.system.UI;
import org.um.feri.ears.algorithms.moo.nsga2.D_NSGAII;
import org.um.feri.ears.problems.StopCriterion;
import org.um.feri.ears.problems.Task;

final class Driver {
  private static final int REFERENCE_TIMEOUT =
      60 * 60 * 1000; // 1 hour timeout for the reference image (just in case)

  public static void main(String[] args) throws Exception {
    // Setup the knobs and display
    UI.verbosity(0);
    BufferedImageDisplay display = new BufferedImageDisplay();
    RenderingKnobs knobs = RenderingKnobs.DEFAULT_KNOBS;
    RenderingConfiguration configuration = RenderingKnobs.DEFAULT_CONFIGURATION;
    RangeConstrainedKnob[] knobsArray =
        Arrays.stream(knobs.asArray())
            .map(RangeConstrainedKnob::new)
            .toArray(RangeConstrainedKnob[]::new);

    // generate the reference
    ConfigurableScene scene =
        args.length == 0
            ? new CornellBox(knobs, configuration, display, REFERENCE_TIMEOUT)
            : new ConfigurableFileScene(knobs, configuration, display, REFERENCE_TIMEOUT, args[0]);
    Instant start = Instant.now();
    scene.run();
    int timeOut = (int) (3 * Duration.between(start, Instant.now()).toMillis() / 2);
    // System.out.println("TIMEOUT IS " + timeOut);

    try {
      File imageFile = new File("/tmp/flora/reference.png");
      ImageIO.write(display.getImage(), "png", imageFile);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // setup the meters with the reference
    ImageDistanceMeter imageMeter =
        new ImageDistanceMeter(display, display.getImage(), ImageDistanceScore.MSE);
    Map<String, Meter> meters =
        Map.of(
            "energy",
            EflectMeter.newLocalMeter(4),
            "runtime",
            new Stopwatch(),
            "score",
            imageMeter);

    // wire everything together
    Machine machine =
        new Machine() {
          @Override
          public Map<String, Meter> meters() {
            return new HashMap<>(meters);
          }
        };

    scene =
        args.length == 0
            ? new CornellBox(knobs, configuration, display, timeOut)
            : new ConfigurableFileScene(knobs, configuration, display, timeOut, args[0]);
    CompatNumberProblem problem =
        new CompatNumberProblem("rendering-problem", new ConfigurableSceneFactory(scene), machine);

    // run the renderer
    D_NSGAII nsga = new D_NSGAII();
    nsga.execute(new Task<>(problem, StopCriterion.EVALUATIONS, 10000, 0, 0));

    // int i = 0;
    // for (BufferedImage image : imageMeter.images) {
    //   try {
    //     File imageFile = new File(String.format("/tmp/flora/%d.png", i++));
    //     ImageIO.write(image, "png", imageFile);
    //   } catch (IOException e) {
    //     e.printStackTrace();
    //   }
    // }
  }
}
