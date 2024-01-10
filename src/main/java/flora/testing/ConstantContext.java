package flora.testing;

import flora.WorkloadContext;

/** A context that always returns the same value. */
public final class ConstantContext<K> implements WorkloadContext<K, K> {
  private final K knobs;
  private final K configuration;

  public ConstantContext(K knobs) {
    this.knobs = knobs;
    this.configuration = knobs;
  }

  @Override
  public K knobs() {
    return knobs;
  }

  @Override
  public K configuration() {
    return configuration;
  }
}
