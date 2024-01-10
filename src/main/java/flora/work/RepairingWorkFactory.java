package flora.work;

import flora.WorkFactory;
import flora.WorkUnit;

public interface RepairingWorkFactory<K, C, W extends WorkUnit<K, C>> extends WorkFactory<K, C, W> {
  /** Checks if a configuration will produce a valid work unit from the given configuration. */
  boolean isValidConfiguration(int[] configuration);

  /** Adjusts a given configuration so that it can produce a valid work unit. */
  int[] repairConfiguration(int[] configuration);
}
