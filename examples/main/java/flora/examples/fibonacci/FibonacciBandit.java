package flora.examples.fibonacci;

import flora.strategy.mab.MultiArmedBandit;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A {@link MultiArmedBandit} whose reward is the operation throughput of a concurrent execution of
 * the naive fibonacci algorithm. Throughput for k threads that compute the nth fibonacci number
 * over some time interval is estimated as $k * \phi^{n} / time$.
 */
public final class FibonacciBandit
    extends MultiArmedBandit<FibonacciKnobs, FibonacciConfiguration, FibonacciBandit> {
  private static final double PHI = (1 + Math.sqrt(5)) / 2;
  private static final double LOG_PHI = Math.log(PHI);

  /**
   * Computes the log of the operation throughput of the configuration. $k * \phi^{n} / time ->
   * log_{\phi}(k * \phi^{n} / time) = n + log_{\phi}(k / time)$
   */
  public static double logThroughput(FibonacciConfiguration configuration, double time) {
    int k = configuration.threads();
    int n = configuration.n();
    return n + Math.log(k / time) / LOG_PHI;
  }

  private final FibonacciKnobs knobs;

  public FibonacciBandit(FibonacciKnobs knobs) {
    this.knobs = knobs;
    withConfiguration(FibonacciConfiguration.DEFAULT);
  }

  public FibonacciBandit(FibonacciKnobs knobs, FibonacciConfiguration configuration) {
    this.knobs = knobs;
    withConfiguration(configuration);
  }

  /** Returns the stored knobs. */
  @Override
  public FibonacciKnobs knobs() {
    return knobs;
  }

  /** Rewards the configuration with the log of the operation throughput. */
  @Override
  protected double reward(FibonacciBandit context, Map<String, Double> measurement) {
    return logThroughput(context.configuration(), measurement.get("stopwatch"));
  }

  /** Creates a new bandit with the same knobs and a random configuration. */
  @Override
  public FibonacciBandit random() {
    return new FibonacciBandit(
        knobs,
        new FibonacciConfiguration(
            knobs
                .threads()
                .fromIndex(
                    ThreadLocalRandom.current().nextInt(knobs.threads().configurationCount()),
                    Integer.class),
            knobs
                .n()
                .fromIndex(
                    ThreadLocalRandom.current().nextInt(knobs.n().configurationCount()),
                    Integer.class)));
  }
}
