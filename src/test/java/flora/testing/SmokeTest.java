package flora.testing;

import static org.junit.Assert.assertEquals;

import flora.Machine;
import java.time.Duration;
import java.time.Instant;
import org.junit.Test;

public class SmokeTest {
  /** This smoke test ensures that the {@link Machine} class runs with minimal expectations. */
  @Test
  public void smokeTest() {
    Instant start = Instant.now();
    Machine machine = new SleepingMachine(1000, DoNothingStrategy.getInstance());
    machine.run();
    Duration sleepTime = Duration.between(start, Instant.now());
    assertEquals(sleepTime.compareTo(Duration.ofSeconds(1)) > -1, true);
  }
}
