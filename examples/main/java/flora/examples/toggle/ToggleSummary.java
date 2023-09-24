package flora.examples.toggle;

import flora.strategy.archiving.RandomArchivingStrategy;

public final class ToggleSummary {
  public static void main(String[] args) {
    RandomArchivingStrategy<?, ?, ToggleContext> strategy =
        new RandomArchivingStrategy<>(ToggleContext.defaultContext());
    ToggleMachine machine = new ToggleMachine();
    for (int i = 0; i < 1000; i++) {
      ToggleContext context = strategy.context();
      strategy.update(context, machine.run(context));
    }
    System.out.println(strategy.summary());
  }
}
