package flora.fault;

import flora.PerformanceFault;
import java.time.Duration;

public final class TimeOutFault extends PerformanceFault {
  private final Duration deadline;
  private final Duration runtime;

  public TimeOutFault(Duration deadline, Duration runtime) {
    this.deadline = deadline;
    this.runtime = runtime;
  }

  @Override
  public String description() {
    return String.format(
        "{\"deadline\":%s,\"runtime\":%s}", deadline.toMillis(), runtime.toMillis());
  }
}
