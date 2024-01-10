// package flora.experiments.sunflow.renderer;

// import flora.KnobContext;
// import flora.Machine;
// import flora.Meter;
// import flora.Strategy;
// import flora.experiments.sunflow.ConfigurableScene;
// import flora.experiments.sunflow.RenderingConfiguration;
// import flora.experiments.sunflow.RenderingKnobs;
// import flora.experiments.sunflow.image.BufferedImageDisplay;
// import flora.experiments.sunflow.image.ImageDistanceMeter;
// import flora.experiments.sunflow.image.ImageDistanceScore;
// import flora.machine.ComposedMachine;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Set;
// import java.util.function.Function;
// import org.sunflow.SunflowAPI;
// import org.sunflow.core.Display;

// /** A {@link Machine} that renders an image from the knobs in {@link ConfigurableScene}. */
// public final class RenderingMachine<KC extends KnobContext<RenderingKnobs, RenderingConfiguration>>
//     extends ComposedMachine<RenderingKnobs, RenderingConfiguration, KC> {
//   /** Helper to automatically create a reference image meter using the default knobs. */
//   public static <KC extends KnobContext<RenderingKnobs, RenderingConfiguration>>
//       RenderingMachine<KC> withReferenceScore(
//           Map<String, Meter> meters,
//           Strategy<RenderingKnobs, RenderingConfiguration, KC> strategy,
//           Function<RenderingConfiguration, ConfigurableScene> sceneFactory,
//           ImageDistanceScore score) {
//     return withReferenceScores(meters, strategy, sceneFactory, Set.of(score));
//   }

//   /** Helper to automatically create a reference image meter using the default knobs. */
//   public static <KC extends KnobContext<RenderingKnobs, RenderingConfiguration>>
//       RenderingMachine<KC> withReferenceScores(
//           Map<String, Meter> meters,
//           Strategy<RenderingKnobs, RenderingConfiguration, KC> strategy,
//           Function<RenderingConfiguration, ConfigurableScene> sceneFactory,
//           Set<ImageDistanceScore> scores) {
//     // make a reference image
//     BufferedImageDisplay display = new BufferedImageDisplay();
//     ConfigurableScene scene = sceneFactory.apply(RenderingConfiguration.defaultConfiguration());
//     scene.build();
//     scene.render(SunflowAPI.DEFAULT_OPTIONS, display);

//     // add the new meter
//     meters = new HashMap<>(meters);
//     for (ImageDistanceScore score : scores) {
//       meters.put(score.name(), new ImageDistanceMeter(display, display.getImage(), score));
//     }
//     // return the machine with the new meters
//     return new RenderingMachine<>(meters, strategy, sceneFactory, display);
//   }

//   private final Strategy<RenderingKnobs, RenderingConfiguration, KC> strategy;
//   private final Function<RenderingConfiguration, ConfigurableScene> sceneFactory;
//   private final Display display;

//   public RenderingMachine(
//       Map<String, Meter> meters,
//       Strategy<RenderingKnobs, RenderingConfiguration, KC> strategy,
//       Function<RenderingConfiguration, ConfigurableScene> sceneFactory,
//       Display display) {
//     super(meters, strategy);
//     this.strategy = strategy;
//     this.sceneFactory = sceneFactory;
//     this.display = display;
//   }

//   @Override
//   public final void runWorkload(KC context) {
//     ConfigurableScene scene = sceneFactory.apply(context.configuration());
//     scene.build();
//     scene.render(SunflowAPI.DEFAULT_OPTIONS, this.display);
//   }
// }
