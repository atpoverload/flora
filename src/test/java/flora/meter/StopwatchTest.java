package flora.meter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import flora.Meter;
import java.time.Duration;
import java.time.Instant;
import org.junit.Test;

public class StopwatchTest {
  private static final long SLEEP_TIME_MS = 100;
  private static final double SLEEP_TIME_SEC = (double) SLEEP_TIME_MS / 1000;
  // TODO: this test is flaky, so there's a big threshold
  private static final double THRESHOLD = SLEEP_TIME_SEC / 2;

  @Test
  public void read_unused() {
    Meter meter = new Stopwatch();

    assertEquals(0.0, meter.read(), 0.0);
  }

  @Test
  public void startStop() {
    Meter meter = new Stopwatch();

    Instant start = Instant.now();
    meter.start();
    meter.stop();
    Duration elapsed = Duration.between(start, Instant.now());

    // check for idempotence
    assertEquals(meter.read(), meter.read(), 0.0);

    long seconds = elapsed.getSeconds();
    long nano = elapsed.getNano();
    double expected = seconds + (double) nano / 10E8;

    assertTrue(0.0 < meter.read());
    assertTrue(meter.read() < expected);
  }

  @Test
  public void startSleepRead() throws Exception {
    Meter meter = new Stopwatch();

    meter.start();
    Thread.sleep(SLEEP_TIME_MS);
    assertEquals(SLEEP_TIME_SEC, meter.read(), THRESHOLD);
    Thread.sleep(SLEEP_TIME_MS);
    meter.stop();
    assertEquals(2 * SLEEP_TIME_SEC, meter.read(), THRESHOLD);
  }

  @Test
  public void startSleepStop() throws Exception {
    Meter meter = new Stopwatch();

    meter.start();
    Thread.sleep(SLEEP_TIME_MS);
    meter.stop();

    assertEquals(SLEEP_TIME_SEC, meter.read(), THRESHOLD);
  }

  @Test
  public void startStartSleepStop() throws Exception {
    Meter meter = new Stopwatch();

    meter.start();
    meter.start();
    Thread.sleep(SLEEP_TIME_MS);
    meter.stop();

    assertEquals(SLEEP_TIME_SEC, meter.read(), THRESHOLD);
  }

  @Test
  public void startSleepStartStop() throws Exception {
    Meter meter = new Stopwatch();

    meter.start();
    Thread.sleep(SLEEP_TIME_MS);
    meter.start();
    meter.stop();

    assertEquals(SLEEP_TIME_SEC, meter.read(), THRESHOLD);
  }

  @Test
  public void startSleepStartSleepStop() throws Exception {
    Meter meter = new Stopwatch();

    meter.start();
    Thread.sleep(SLEEP_TIME_MS);
    meter.start();
    Thread.sleep(SLEEP_TIME_MS);
    meter.stop();

    assertEquals(2 * SLEEP_TIME_SEC, meter.read(), THRESHOLD);
  }
}
