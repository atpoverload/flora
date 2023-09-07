package flora.experiments.sunflow.renderer;

import flora.experiments.sunflow.ConfigurableScene;
import flora.experiments.sunflow.KillableConfigurableRenderingMachine;
import flora.experiments.sunflow.MseBandit;
import flora.experiments.sunflow.scenes.CornellBox;
import flora.meter.Stopwatch;
import flora.meter.contrib.EflectMeter;
import flora.strategy.mab.MultiArmedBanditStrategy;
import flora.strategy.mab.epsilon.RandomEpsilonPolicy;
import flora.strategy.mab.exploit.LowestConfiguration;
import java.util.Map;
import org.sunflow.system.UI;

/** Driver that renders a Cornell box using a given strategy. */
public final class KillingSceneRenderer {
  public static void main(String[] args) {
    UI.verbosity(0);
    KillableConfigurableRenderingMachine machine =
        KillableConfigurableRenderingMachine.withMseReference(
            Map.of("stopwatch", new Stopwatch(), "eflect", EflectMeter.newLocalMeter(4)),
            new MultiArmedBanditStrategy(
                new MseBandit(ConfigurableScene.getKnobs()),
                new RandomEpsilonPolicy(0.10, "first"),
                LowestConfiguration.getPolicy()),
            CornellBox::new);
    for (int i = 0; i < 100; i++) {
      machine.run();
    }

    System.out.println(machine);
  }
}
