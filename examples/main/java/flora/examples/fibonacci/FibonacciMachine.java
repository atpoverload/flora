package flora.examples.fibonacci;

import static java.util.concurrent.Executors.newFixedThreadPool;

import flora.Machine;
import flora.Strategy;
import flora.knob.IntRangeKnob;
import flora.machine.ComposedMachine;
import flora.meter.CpuJiffiesMeter;
import flora.meter.Stopwatch;
import flora.strategy.archiving.RandomArchivingStrategy;
import flora.strategy.mab.MultiArmedBanditStrategy;
import flora.strategy.mab.epsilon.RandomEpsilonPolicy;
import flora.strategy.mab.exploit.HighestConfiguration;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/** A {@link Machine} to find the optimal throughput for concurrent naive fibonaccis. */
final class FibonacciMachine
    extends ComposedMachine<FibonacciKnobs, FibonacciConfiguration, FibonacciBandit> {
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

  private static int fibonacci(int n) {
    if (n == 0 || n == 1) {
      return n;
    } else {
      return fibonacci(n - 1) + fibonacci(n - 2);
    }
  }

  private final ExecutorService executor =
      newFixedThreadPool(
          CPU_COUNT,
          r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
          });

  private FibonacciMachine() {
    super(Map.of("stopwatch", new Stopwatch(), "jiffies", new CpuJiffiesMeter()));
  }

  /** Compute the nth fibonacci number. Multiple threads will separately compute the same value. */
  @Override
  public void runWorkload(FibonacciBandit context) {
    int k = context.configuration().threads();
    int n = context.configuration().n();
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

  private static FibonacciBandit parseArgs(String[] args) {
    int nFirst = 25;
    int nLast = 35;
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

    return new FibonacciBandit(
        new FibonacciKnobs(new IntRangeKnob(minCpus, maxCpus), new IntRangeKnob(nFirst, nLast)),
        new FibonacciConfiguration(minCpus, nFirst));
  }

  public static void main(String[] args) {
    FibonacciBandit context = parseArgs(args);

    RandomArchivingStrategy<FibonacciKnobs, FibonacciConfiguration, FibonacciBandit> strategy =
        new RandomArchivingStrategy<>(context);
    // runWith(strategy);
    // System.out.println(strategy.summary());
    runWith(
        new MultiArmedBanditStrategy<>(
            context, new RandomEpsilonPolicy(0.20, "greedy"), HighestConfiguration.instance()));
    context.rewardedConfigurations().stream()
        .sorted()
        .forEach(
            c ->
                System.out.println(
                    String.format(
                        "%-49s = %.6f (%d)",
                        c, context.averageReward(c), context.rewardedCount(c))));
  }

  private static void runWith(
      Strategy<FibonacciKnobs, FibonacciConfiguration, FibonacciBandit> strategy) {
    System.out.println(String.format("Running with %s", strategy.getClass().getSimpleName()));
    FibonacciMachine machine = new FibonacciMachine();
    Instant ts = Instant.now();
    while (Duration.between(ts, Instant.now()).toSeconds() < 300) {
      var context = strategy.context();
      strategy.update(context, machine.run(context));
    }
  }
}
