package flora.work;

import flora.WorkFactory;
import flora.WorkUnit;

public interface RandomizableWorkFactory<K, C, W extends WorkUnit<K, C>>
    extends WorkFactory<K, C, W> {
  /** Creates a configuration which is chosen randomly. */
  int[] randomConfiguration();

  /** Creates a work unit whose configuration is chosen randomly. */
  default W randomWorkUnit() {
    return this.newWorkUnit(randomConfiguration());
  }
}
