package flora.experiments.sunflow.renderers;

import flora.MeteringMachine;
import flora.contrib.ears.FloraProblem;
import flora.experiments.sunflow.image.ImageDistanceScore;
import flora.experiments.sunflow.image.ImagePiqeMeter;
import flora.experiments.sunflow.scene.RenderingConfiguration;
import flora.experiments.sunflow.scene.RenderingKnobs;
import flora.experiments.sunflow.scene.Scene;
import flora.experiments.sunflow.scene.SquareSceneFactory;
import flora.experiments.sunflow.scene.util.JsonSceneUtil;
import flora.util.JsonUtil;
import flora.util.LoggerUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.JSONObject;
import org.sunflow.system.UI;
import org.um.feri.ears.algorithms.moo.nsga2.D_NSGAII;
import org.um.feri.ears.problems.StopCriterion;
import org.um.feri.ears.problems.Task;

final class SunflowRenderingProblem {
  private static final Logger logger = LoggerUtil.getLogger();

  public static void main(String[] args) throws Exception {
    UI.verbosity(0);

    RenderingArgs renderingArgs = RenderingArgs.fromArgs(args);
    logger.info(String.format("running scene %s", renderingArgs.cmd.getOptionValue("scene")));
    logger.info(String.format("with knobs %s", JsonSceneUtil.toJson(renderingArgs.engine.knobs())));
    MeteringMachine machine =
        new MeteringMachine(renderingArgs.engine.createMeters(ImageDistanceScore.MSE));

    // wire everything together
    FloraProblem<RenderingKnobs, RenderingConfiguration, Scene> problem =
        new FloraProblem<>(
            "sunflow-render",
            new SquareSceneFactory(renderingArgs.engine.knobs(), renderingArgs.engine::newScene),
            machine);
    D_NSGAII nsga = new D_NSGAII();
    if (renderingArgs.cmd.hasOption("load")) {
      logger.info(
          String.format(
              "starting from provided state %s", renderingArgs.cmd.getOptionValue("load")));
      nsga.loadState(renderingArgs.cmd.getOptionValue("load"), true);
    }

    // run
    int iterations = Integer.parseInt(renderingArgs.cmd.getOptionValue("iterations"));
    logger.info(String.format("running for %d iterations", iterations));
    nsga.execute(new Task<>(problem, StopCriterion.EVALUATIONS, iterations, 0, 0));

    // dump data
    if (renderingArgs.cmd.hasOption("output")) {
      logger.info(
          String.format("writing result to %s", renderingArgs.cmd.getOptionValue("output")));
      JSONObject data = JsonUtil.toJson(problem.getCollector(), JsonSceneUtil::toJson);
      try (PrintWriter writer =
          new PrintWriter(
              Path.of(renderingArgs.cmd.getOptionValue("output"), "results.json").toFile())) {
        writer.println(data);
      } catch (Exception e) {
      }
    }

    if (renderingArgs.cmd.hasOption("output")) {
      logger.info(String.format("writing state to %s", renderingArgs.cmd.getOptionValue("output")));
      nsga.saveState(Path.of(renderingArgs.cmd.getOptionValue("output"), "state.json").toString());
    }

    if (renderingArgs.cmd.hasOption("output")) {
      logger.info(
          String.format(
              "writing rendered images to %s", renderingArgs.cmd.getOptionValue("output")));
      String scene = new File(renderingArgs.cmd.getOptionValue("scene")).getName().split("\\.")[0];
      int i = 0;
      for (BufferedImage image : ((ImagePiqeMeter) machine.getMeter("piqe")).images) {
        try {
          Path imageFilePath =
              Files.createFile(
                  Path.of(
                      renderingArgs.cmd.getOptionValue("output"), String.format("%d.png", i++)));
          ImageIO.write(image, "png", imageFilePath.toFile());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      logger.info(
          String.format("wrote %d images to %s", i, renderingArgs.cmd.getOptionValue("output")));
    }
  }
}
