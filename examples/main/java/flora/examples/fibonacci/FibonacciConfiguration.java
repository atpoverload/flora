package flora.examples.fibonacci;

/** Configuration for the {@link FibonacciBandit}. */
final record FibonacciConfiguration(int threads, int n)
    implements Comparable<FibonacciConfiguration> {
  @Override
  public int compareTo(FibonacciConfiguration other) {
    int threadCompare = Integer.compare(threads, other.threads);
    if (threadCompare == 0) {
      return Integer.compare(n, other.n);
    }
    return threadCompare;
  }
}
