package flora.fault;

import flora.PerformanceFault;

public final class ConstraintFault extends PerformanceFault {
  private final double value;
  private final double constraint;

  public ConstraintFault(double value, double constraint) {
    this.value = value;
    this.constraint = constraint;
  }

  @Override
  public String description() {
    return String.format("{\"value\":%f, \"constraint\":%f", value, constraint);
  }
}
