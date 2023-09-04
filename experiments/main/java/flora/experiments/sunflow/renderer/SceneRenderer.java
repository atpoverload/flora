package flora.experiments.sunflow.renderer;

import flora.experiments.sunflow.ConfigurableRenderingMachine;
import flora.experiments.sunflow.ConfigurableScene;
import flora.experiments.sunflow.MseBandit;
import flora.experiments.sunflow.scenes.CornellBox;
import flora.knob.Knobs;
import flora.meter.Stopwatch;
import flora.strategy.mab.MultiArmedBanditStrategy;
import flora.strategy.mab.epsilon.RandomEpsilonPolicy;
import flora.strategy.mab.exploit.LowestConfiguration;
import java.util.Map;
import org.sunflow.system.UI;

/** Driver that renders a Cornell box using a given strategy. */
public final class SceneRenderer {
  public static void main(String[] args) {
    UI.verbosity(0);
    int configs = Knobs.getConfigurationCount(ConfigurableScene.getKnobs());
    System.out.println(String.format("Sunflow configs: %d", configs));
    ConfigurableRenderingMachine machine =
        ConfigurableRenderingMachine.withMseReference(
            Map.of("stopwatch", new Stopwatch()),
            // new RandomArchivingStrategy(ConfigurableScene.getKnobs()),
            new MultiArmedBanditStrategy(
                new MseBandit(ConfigurableScene.getKnobs()),
                new RandomEpsilonPolicy(0.10, "first"),
                LowestConfiguration.getPolicy()),
            CornellBox::new);
    for (int i = 0; i < 100 * configs; i++) {
      machine.run();
      if ((i % (configs / 4) + 1) == 0) {
        System.out.println(String.format("%d iters done", i));
      }
    }

    System.out.println(machine);
  }
}
