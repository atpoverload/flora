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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sunflow.system.UI;
import org.um.feri.ears.algorithms.moo.nsga2.D_NSGAII;
import org.um.feri.ears.problems.StopCriterion;
import org.um.feri.ears.problems.Task;

public final class SunflowRenderingReplayProblem {
  public static void main(String[] args) throws Exception {
    UI.verbosity(0);
    LoggerUtil.getLogger().setLevel(Level.FINER);

    RenderingArgs renderingArgs = RenderingArgs.fromArgs(args);
    SquareSceneFactory sceneFactory =
        new SquareSceneFactory(renderingArgs.engine.knobs(), renderingArgs.engine::newScene);

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
      System.out.println("????");
      System.out.println(renderingArgs.cmd.getOptionValue("reference_snapshot"));
      nsga.loadState(renderingArgs.cmd.getOptionValue("reference_snapshot"), false);
    }

    // run
    nsga.execute(new Task<>(problem, StopCriterion.EVALUATIONS, 1000, 0, 0));

    // dump data
    if (renderingArgs.cmd.hasOption("output")) {
      JSONObject data = JsonUtil.toJson(problem.getCollector(), JsonSceneUtil::toJson);
      try (PrintWriter writer = new PrintWriter(renderingArgs.cmd.getOptionValue("output"))) {
        writer.println(data);
      } catch (Exception e) {
      }
    }
    if (renderingArgs.cmd.hasOption("snapshot")) {
      nsga.saveState(renderingArgs.cmd.getOptionValue("snapshot"));
    }
  }
}
