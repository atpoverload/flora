package flora.experiments.sunflow.renderers;

import flora.MeteringMachine;
import flora.contrib.ears.FloraProblem;
import flora.experiments.sunflow.image.ImageDistanceScore;
import flora.experiments.sunflow.scene.RenderingConfiguration;
import flora.experiments.sunflow.scene.RenderingKnobs;
import flora.experiments.sunflow.scene.Scene;
import flora.experiments.sunflow.scene.SquareSceneFactory;
import flora.experiments.sunflow.scene.util.JsonSceneUtil;
import flora.util.JsonUtil;
import flora.util.LoggerUtil;
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
    LoggerUtil.getLogger()
        .info(String.format("running scene %s", renderingArgs.cmd.getOptionValue("scene")));
    LoggerUtil.getLogger().info(String.format("with knobs %s", renderingArgs.engine.knobs()));
    MeteringMachine machine =
        new MeteringMachine(renderingArgs.engine.createMeters(ImageDistanceScore.MSE));

    // wire everything together
    FloraProblem<RenderingKnobs, RenderingConfiguration, Scene> problem =
        new FloraProblem<>(
            "sunflow-rendering",
            new SquareSceneFactory(renderingArgs.engine.knobs(), renderingArgs.engine::newScene),
            machine);
    D_NSGAII nsga = new D_NSGAII();
    if (renderingArgs.cmd.hasOption("reference_snapshot")) {
      LoggerUtil.getLogger()
          .info(
              String.format(
                  "starting from provided state %s",
                  renderingArgs.cmd.getOptionValue("reference_snapshot")));
      nsga.loadState(renderingArgs.cmd.getOptionValue("reference_snapshot"), true);
    }

    // run
    nsga.execute(new Task<>(problem, StopCriterion.EVALUATIONS, 10000, 0, 0));

    // dump data
    if (renderingArgs.cmd.hasOption("output")) {
      LoggerUtil.getLogger()
          .info(String.format("writing result to %s", renderingArgs.cmd.getOptionValue("output")));
      JSONObject data = JsonUtil.toJson(problem.getCollector(), JsonSceneUtil::toJson);
      try (PrintWriter writer = new PrintWriter(renderingArgs.cmd.getOptionValue("output"))) {
        writer.println(data);
      } catch (Exception e) {
      }
    }
    if (renderingArgs.cmd.hasOption("snapshot")) {
      LoggerUtil.getLogger()
          .info(String.format("writing state to %s", renderingArgs.cmd.getOptionValue("snapshot")));
      nsga.saveState(renderingArgs.cmd.getOptionValue("snapshot"));
    }
  }
}
