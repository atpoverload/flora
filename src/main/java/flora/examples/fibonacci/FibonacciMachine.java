package flora.examples.fibonacci;

import static java.util.concurrent.Executors.newFixedThreadPool;

import flora.Knob;
import flora.Machine;
import flora.Strategy;
import flora.knob.IntRangeKnob;
import flora.meter.Stopwatch;
import flora.strategy.RandomArchivingStrategy;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/** A {@link Machine} to find the optimal throughput for concurrent naive fibonaccis. */
final class FibonacciMachine extends Machine {
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

  private static int fibonacci(int n) {
    if (n == 0 || n == 1) {
      return n;
    } else {
      return fibonacci(n - 1) + fibonacci(n - 2);
    }
  }

  static Map<String, Knob> newFibonacciKnobs(int minCpus, int maxCpus, int nFirst, int nLast) {
    return Map.of(
        "threads", new IntRangeKnob(minCpus, maxCpus), "n", new IntRangeKnob(nFirst, nLast));
  }

  private final ExecutorService executor =
      newFixedThreadPool(
          CPU_COUNT,
          r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
          });
  private final Strategy strategy;

  private FibonacciMachine(Strategy strategy) {
    super(Map.of("stopwatch", new Stopwatch()), strategy);
    this.strategy = strategy;
  }

  /** Compute the nth fibonacci number. Multiple threads will separately compute the same value. */
  @Override
  protected void runWorkload(Map<String, Knob> knobs) {
    int k = knobs.get("threads").getInt();
    int n = knobs.get("n").getInt();
    ArrayList<Future<?>> futures = new ArrayList<>();
    for (int i = 0; i < k; i++) {
      futures.add(executor.submit(() -> fibonacci(n)));
    }

    for (Future<?> future : futures) {
      try {
        future.get();
      } catch (Exception e) {
        System.out.println("Apparently we failed?");
        e.printStackTrace();
      }
    }
  }

  /** Returns the evaluations made by the bandit aggregated by knob configuration. */
  @Override
  public String toString() {
    return strategy.toString();
  }

  public static void main(String[] args) {
    int nFirst = 35;
    int nLast = 40;
    if (args.length != 0 && args.length != 2) {
      throw new IllegalArgumentException(
          String.format("expects zero or two arguments, got %s", Arrays.toString(args)));
    } else if (args.length == 2) {
      nFirst = Integer.parseInt(args[0]);
      nLast = Integer.parseInt(args[1]);
    }
    if (nFirst >= nLast) {
      throw new IllegalArgumentException(
          String.format("first (%d) must be less than last (%d)", nFirst, nLast));
    }
    int minCpus = CPU_COUNT / 2;
    int maxCpus = 3 * CPU_COUNT / 2;

    System.out.println(
        runWith(new RandomArchivingStrategy(newFibonacciKnobs(minCpus, maxCpus, nFirst, nLast))));
  }

  private static FibonacciMachine runWith(Strategy strategy) {
    System.out.println(String.format("Running with %s", strategy.getClass().getSimpleName()));
    FibonacciMachine machine = new FibonacciMachine(strategy);
    Instant ts = Instant.now();
    while (Duration.between(ts, Instant.now()).toSeconds() < 300) machine.run();
    return machine;
  }
}
