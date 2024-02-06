package flora;

public abstract class PerformanceFault extends RuntimeException {
  public abstract String description();

  @Override
  public final String toString() {
    return description();
  }
}
