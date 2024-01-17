package flora.experiments.sunflow.renderers;

import flora.Machine;
import flora.contrib.ears.FloraProblem;
import flora.experiments.sunflow.image.ImageDistanceScore;
import flora.experiments.sunflow.scene.RenderingConfiguration;
import flora.experiments.sunflow.scene.RenderingKnobs;
import flora.experiments.sunflow.scene.Scene;
import flora.experiments.sunflow.scene.SquareSceneFactory;
import flora.experiments.sunflow.scene.util.JsonSceneUtil;
import flora.util.JsonUtil;
import java.io.PrintWriter;
import org.json.JSONObject;
import org.sunflow.system.UI;
import org.um.feri.ears.algorithms.moo.nsga2.D_NSGAII;
import org.um.feri.ears.problems.StopCriterion;
import org.um.feri.ears.problems.Task;

final class SunflowRenderingProblem {
  public static void main(String[] args) throws Exception {
    UI.verbosity(0);

    RenderingArgs renderingArgs = RenderingArgs.fromArgs(args);
    Machine machine = new Machine(renderingArgs.engine.createMeters(ImageDistanceScore.MSE));

    // wire everything together
    FloraProblem<RenderingKnobs, RenderingConfiguration, Scene> problem =
        new FloraProblem<>(
            new SquareSceneFactory(renderingArgs.engine.knobs(), renderingArgs.engine::newScene),
            machine);
    D_NSGAII nsga = new D_NSGAII();
    nsga.execute(new Task<>(problem, StopCriterion.EVALUATIONS, 10000, 0, 0));

    if (renderingArgs.cmd.hasOption("output")) {
      JSONObject data = JsonUtil.toJson(problem.getCollector(), JsonSceneUtil::toJson);
      try (PrintWriter writer = new PrintWriter(renderingArgs.cmd.getOptionValue("output"))) {
        writer.println(data);
      } catch (Exception e) {
      }
    }
  }
}
