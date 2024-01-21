package flora.experiments.sunflow.renderers;

import flora.MeteringMachine;
import flora.contrib.ears.FloraReplayProblem;
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

    ArrayList<int[]> configurations = new ArrayList<>();
    for (String configFile : renderingArgs.cmd.getArgs()) {
      for (Object config : new JSONArray(Files.readString(Paths.get(configFile)))) {
        RenderingConfiguration configuration =
            JsonSceneUtil.parseConfiguration((JSONObject) config);
        configurations.add(sceneFactory.decode(configuration));
      }
    }

    MeteringMachine machine =
        new MeteringMachine(renderingArgs.engine.createMeters(ImageDistanceScore.MSE));
    FloraReplayProblem<RenderingKnobs, RenderingConfiguration, Scene> problem =
        new FloraReplayProblem<>("sunflow-rendering", sceneFactory, machine, configurations);
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
