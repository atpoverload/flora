package flora.strategy.contrib.ears;

import flora.Machine;
import flora.WorkloadContext;
import java.util.Map;
import java.util.function.Function;

public final class EarsMachineAdapter<Ctx extends WorkloadContext<?, ?>> {
  private final Machine<?, ?, Ctx> machine;
  private final Function<EarsContext, Ctx> adapter;

  public EarsMachineAdapter(Machine<?, ?, Ctx> machine, Function<EarsContext, Ctx> adapter) {
    this.machine = machine;
    this.adapter = adapter;
  }

  public Map<String, Double> run(EarsContext strategy) {
    return machine.run(adapter.apply(strategy));
  }
}
