package flora.meter;

import static org.junit.Assert.assertEquals;

import flora.Meter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
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
