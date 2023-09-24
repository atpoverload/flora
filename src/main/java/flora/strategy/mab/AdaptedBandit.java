package flora.strategy.mab;

import flora.context.RandomizableContext;
import java.util.Map;
import java.util.function.ToDoubleBiFunction;

/** A {@link MultiArmedBandit} that adapts a {@link RandomizableContext}. */
public final class AdaptedBandit<K, C, Ctx extends RandomizableContext<K, C, Ctx>>
    extends MultiArmedBandit<K, C, AdaptedBandit<K, C, Ctx>> {
  private final RandomizableContext<K, C, Ctx> context;
  private final ToDoubleBiFunction<AdaptedBandit<K, C, Ctx>, Map<String, Double>> reward;

  public AdaptedBandit(
      RandomizableContext<K, C, Ctx> context,
      ToDoubleBiFunction<AdaptedBandit<K, C, Ctx>, Map<String, Double>> reward) {
    this.context = context;
    this.reward = reward;
  }

  @Override
  public K knobs() {
    return context.knobs();
  }

  @Override
  public double reward(AdaptedBandit<K, C, Ctx> context, Map<String, Double> measurement) {
    return reward.applyAsDouble(context, measurement);
  }

  @Override
  public AdaptedBandit<K, C, Ctx> random() {
    return withConfiguration(context.random().configuration());
  }
}
