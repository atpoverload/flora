package flora.meter;

import static org.junit.Assert.assertEquals;

import flora.Meter;
import org.junit.Test;

public class SnapshotMeterTest {
  @Test
  public void sanityTest() {
    Meter meter =
        new SnapshotMeter() {
          @Override
          public double read() {
            return 1;
          }
        };

    assertEquals(1.0, meter.read(), 0.0);

    meter.start();
    assertEquals(1.0, meter.read(), 0.0);

    meter.stop();
    assertEquals(1.0, meter.read(), 0.0);
  }
}
