package flora.experiments.sunflow.renderer;

import flora.Machine;
import flora.Meter;
import flora.experiments.sunflow.RenderingConfiguration;
import flora.experiments.sunflow.RenderingKnobs;
import flora.experiments.sunflow.image.BufferedImageDisplay;
import flora.experiments.sunflow.image.ImageDistanceMeter;
import flora.experiments.sunflow.image.ImageDistanceScore;
import flora.experiments.sunflow.scenes.CornellBox;
import flora.knob.meta.RangeConstrainedKnob;
import flora.meter.CpuJiffiesMeter;
import flora.meter.Stopwatch;
import flora.strategy.contrib.ears.CompatNumberProblem;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.sunflow.system.UI;
import org.um.feri.ears.algorithms.moo.nsga2.D_NSGAII;
import org.um.feri.ears.problems.StopCriterion;
import org.um.feri.ears.problems.Task;

final class Driver {
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
    CornellBox scene = new CornellBox(knobs, RenderingConfiguration.DEFAULT, display);
    scene.run();

    // setup the meters with the reference
    Map<String, Meter> meters =
        Map.of(
            "stopwatch",
            new Stopwatch(),
            "score",
            new ImageDistanceMeter(display, display.getImage(), ImageDistanceScore.MSE),
            "jiffies",
            new CpuJiffiesMeter());

    // wire everything together
    Machine machine =
        new Machine() {
          @Override
          public Map<String, Meter> meters() {
            return new HashMap<>(meters);
          }
        };

    CompatNumberProblem<RangeConstrainedKnob> problem =
        new CompatNumberProblem<>("rendering-problem", knobsArray, scene, machine);

    // run the renderer
    D_NSGAII nsga = new D_NSGAII();
    nsga.execute(new Task<>(problem, StopCriterion.EVALUATIONS, 100000, 0, 0));
  }
}
