package flora.fault;

import flora.PerformanceFault;
import java.util.Optional;

public final class MemoryFault extends PerformanceFault {
  public enum MemoryFaultType {
    OUT_OF_MEMORY,
  }

  private final MemoryFaultType faultType;
  private final Optional<String> message;

  public MemoryFault(MemoryFaultType faultType) {
    this.faultType = faultType;
    this.message = Optional.empty();
  }

  public MemoryFault(MemoryFaultType faultType, String message) {
    this.faultType = faultType;
    this.message = Optional.of(message);
  }

  @Override
  public String description() {
    if (message.isPresent()) {
      return String.format("{\"type\":%s,\"message\":%s", faultType.name(), message.get());
    } else {
      return String.format("{\"type\":%s", faultType.name());
    }
  }
}
