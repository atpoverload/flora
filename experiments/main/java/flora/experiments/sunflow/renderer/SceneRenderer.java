// package flora.experiments.sunflow.renderer;

// import flora.experiments.sunflow.image.ImageDistanceBandit;
// import flora.experiments.sunflow.image.ImageDistanceScore;
// import flora.experiments.sunflow.scenes.CornellBox;
// import flora.meter.Stopwatch;
// import flora.meter.contrib.EflectMeter;
// import flora.strategy.mab.MultiArmedBanditStrategy;
// import flora.strategy.mab.epsilon.RandomEpsilonPolicy;
// import flora.strategy.mab.exploit.LowestConfiguration;
// import java.util.Map;
// import org.sunflow.system.UI;

// /** Driver that renders a Cornell box using a given strategy. */
// public final class SceneRenderer {
//   public static void main(String[] args) {
//     UI.verbosity(0);
//     RenderingMachine<ImageDistanceBandit> machine =
//         RenderingMachine.withReferenceScore(
//             Map.of("stopwatch", new Stopwatch(), "eflect", EflectMeter.newLocalMeter(4)),
//             new MultiArmedBanditStrategy<>(
//                 new ImageDistanceBandit(ImageDistanceScore.MSE),
//                 new RandomEpsilonPolicy(0.10, "greedy"),
//                 LowestConfiguration.instance()),
//             CornellBox::new,
//             ImageDistanceScore.MSE);
//     for (int i = 0; i < 10; i++) {
//       machine.run();
//     }

//     System.out.println(machine.strategy());
//   }
// }
