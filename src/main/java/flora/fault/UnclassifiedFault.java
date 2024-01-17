package flora.fault;

import flora.PerformanceFault;
import java.util.Arrays;

public final class UnclassifiedFault extends PerformanceFault {
  private final Throwable error;

  public UnclassifiedFault(Throwable error) {
    this.error = error;
  }

  @Override
  public String description() {
    return String.format(
        "{\"type\":\"%s\",\"message\":\"%s\",\"stackTrace\":\"%s\"",
        error.getClass().getName(), error.getMessage(), Arrays.toString(error.getStackTrace()));
  }
}
