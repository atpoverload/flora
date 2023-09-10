package flora.testing;

import flora.KnobContext;

public final class ConstantContext<K> implements KnobContext<K, K> {
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
