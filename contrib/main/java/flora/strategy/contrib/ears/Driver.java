package flora.strategy.contrib.ears;

import flora.Knob;
import flora.examples.toggle.ToggleContext;
import flora.examples.toggle.ToggleKnobs;
import flora.examples.toggle.ToggleMachine;
import java.util.ArrayList;
import java.util.List;

public final class Driver {
  private static ToggleContext createContext(int[] configuration) {
    return ToggleContext.fromIndicies(configuration[0], configuration[1]);
  }

  public static void main(String[] args) {
    ToggleKnobs knobs = ToggleKnobs.INSTANCE;
    CompatNumberProblem<ToggleContext> problem =
        new CompatNumberProblem<>(
            new Knob[] {knobs.toggle1(), knobs.toggle2()},
            new EarsMachineAdapter<>(new ToggleMachine(), s -> createContext(s.configuration())));

    System.out.println(problem.measurements);
    problem.evaluate(List.of(0, 0));
    System.out.println(problem.measurements);

    System.out.println(problem.isFeasible(List.of(0, 0)));

    ArrayList<Integer> solution = new ArrayList<>();
    solution.add(2);
    solution.add(-1);
    System.out.println(problem.isFeasible(solution));

    problem.makeFeasible(solution);
    System.out.println(solution);
    System.out.println(problem.isFeasible(solution));
  }
}
