package flora.examples.fibonacci;

import flora.Knob;
import flora.strategy.mab.MultiArmedBandit;
import java.util.Map;

/**
 * A {@link MultiArmedBandit} whose reward is the operation throughput of a concurrent execution of
 * the naive fibonacci algorithm. Throughput for k threads that compute the nth fibonacci number
 * over some time interval is estimated as $k * \phi^{n} / time$.
 */
class FibonacciBandit implements MultiArmedBandit {
  private static final double PHI = (1 + Math.sqrt(5)) / 2;
  private static final double LOG_PHI = Math.log(PHI);

  private final Map<String, Knob> knobs;

  FibonacciBandit(int minCpus, int maxCpus, int nFirst, int nLast) {
    this.knobs = FibonacciMachine.newFibonacciKnobs(minCpus, maxCpus, nFirst, nLast);
  }

  @Override
  public Map<String, Knob> getKnobs() {
    return knobs;
  }

  /**
   * Computes the log of the operation throughput of the configuration. $k * \phi^{n} / time ->
   * log_{\phi}(k * \phi^{n} / time) = n + log_{\phi}(k / time)$
   */
  @Override
  public double reward(Map<String, Knob> knobs, Map<String, Double> measurement) {
    int k = knobs.get("threads").getInt();
    int n = knobs.get("n").getInt();
    double time = measurement.get("stopwatch");
    return n + Math.log(k / time) / LOG_PHI;
  }
}
