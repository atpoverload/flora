// package flora.experiments.sunflow.image;

// import java.util.Map;
// import java.util.Set;
// import java.util.function.Function;
// import flora.Machine;
// import flora.Meter;
// import flora.Strategy;
// import flora.experiments.sunflow.ConfigurableScene;
// import flora.experiments.sunflow.RenderingConfiguration;
// import flora.experiments.sunflow.RenderingKnobs;
// import flora.experiments.sunflow.renderer.RenderingMachine;

// public final class ImageQoSMachine extends Machine {
//       public static <W extends WorkUnit<RenderingKnobs, RenderingConfiguration>>
//       ImageQoSMachine withReferenceScore(
//           Map<String, Meter> meters,
//           WorkUnit strategy,
//           Function<RenderingConfiguration, ConfigurableScene> sceneFactory,
//           ImageDistanceScore score) {
//     return withReferenceScores(meters, strategy, sceneFactory, score);
//   }

//   private final Map<String, Meter> meters;

//   private ImageQoSMachine(Map<String, Meter> meters) {
//     this.meters = meters;
//   }

//   @Override
//   public Map<String, Meter> meters() {
//     return meters;
//   }
// }
