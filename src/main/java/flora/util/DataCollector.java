package flora.util;

import flora.PerformanceFault;
import flora.fault.UnclassifiedFault;
import java.util.HashMap;
import java.util.Map;

public final class DataCollector<I, C> {
  private final HashMap<I, C> configurations = new HashMap<>();
  private final HashMap<I, Map<String, Double>> measurements = new HashMap<>();
  private final HashMap<I, PerformanceFault> errors = new HashMap<>();

  public void addConfiguration(I index, C configuration) {
    configurations.put(index, configuration);
  }

  public void addMeasurement(I index, Map<String, Double> measurement) {
    measurements.put(index, measurement);
  }

  public void addError(I index, Throwable error) {
    if (error instanceof PerformanceFault) {
      errors.put(index, (PerformanceFault) error);
    } else {
      errors.put(index, new UnclassifiedFault(error));
    }
  }

  public boolean hasConfigurations() {
    return configurations.isEmpty();
  }

  public boolean hasMeasurements() {
    return measurements.isEmpty();
  }

  public boolean hasErrors() {
    return errors.isEmpty();
  }

  public HashMap<I, C> getConfigurations() {
    return new HashMap<>(configurations);
  }

  public HashMap<I, Map<String, Double>> getMeasurements() {
    return new HashMap<>(measurements);
  }

  public HashMap<I, PerformanceFault> getErrors() {
    return new HashMap<>(errors);
  }
}
