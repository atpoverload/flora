package flora.experiments.sunflow.renderers;

import flora.MeteringMachine;
import flora.experiments.sunflow.image.ImageDistanceScore;
import flora.experiments.sunflow.scene.RenderingConfiguration;
import flora.experiments.sunflow.scene.util.JsonSceneUtil;
import flora.util.DataCollector;
import flora.util.JsonUtil;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sunflow.system.UI;

public final class SceneRenderer {
  public static void main(String[] args) throws Exception {
    UI.verbosity(0);

    RenderingArgs renderingArgs = RenderingArgs.fromArgs(args);
    MeteringMachine machine =
        new MeteringMachine(renderingArgs.engine.createMeters(ImageDistanceScore.MSE));
    DataCollector<Instant, RenderingConfiguration> collector = new DataCollector<>();

    for (String configFile : renderingArgs.cmd.getArgs()) {
      for (Object config : new JSONArray(Files.readString(Paths.get(configFile)))) {
        Instant timestamp = Instant.now();
        RenderingConfiguration configuration =
            JsonSceneUtil.parseConfiguration((JSONObject) config);
        collector.addConfiguration(timestamp, configuration);
        try {
          collector.addMeasurement(
              timestamp, machine.run(renderingArgs.engine.newScene(configuration)));
        } catch (Exception e) {
          collector.addError(timestamp, e);
        }
      }
    }

    if (renderingArgs.cmd.hasOption("output")) {
      JSONObject data = JsonUtil.toJson(collector, JsonSceneUtil::toJson);
      try (PrintWriter writer = new PrintWriter(renderingArgs.cmd.getOptionValue("output"))) {
        writer.println(data);
      } catch (Exception e) {
      }
    }
  }
}
