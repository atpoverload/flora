package flora.testing;

import flora.Strategy;
import flora.WorkloadContext;
import java.util.Map;

public class SettableStrategy<K, C, Ctx extends WorkloadContext<K, C>>
    implements Strategy<K, C, Ctx> {
  private Ctx context;

  public SettableStrategy(Ctx context) {
    this.context = context;
  }

  @Override
  public Ctx context() {
    return this.context;
  }

  @Override
  public void update(Ctx context, Map<String, Double> measurement) {
    setContext(context);
  }

  public void setContext(Ctx context) {
    this.context = context;
  }
}
