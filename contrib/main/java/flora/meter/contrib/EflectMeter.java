package flora.meter.contrib;

import static java.util.concurrent.Executors.newScheduledThreadPool;

import eflect.Eflect;
import eflect.LocalEflect;
import eflect.SingletonEflect;
import eflect.Virtualization;
import eflect.Virtualization.VirtualizedComponent;
import flora.Meter;
import java.util.concurrent.atomic.AtomicInteger;

public class EflectMeter implements Meter {
  private static final AtomicInteger counter = new AtomicInteger();

  public static EflectMeter newLocalMeter(long periodMillis) {
    return new EflectMeter(
        new LocalEflect(
            periodMillis,
            newScheduledThreadPool(
                3,
                r -> {
                  Thread t =
                      new Thread(r, String.format("eflect-meter-%d", counter.getAndIncrement()));
                  t.setDaemon(true);
                  return t;
                })));
  }

  public static EflectMeter singletonMeter() {
    return new EflectMeter(SingletonEflect.getInstance());
  }

  private final Eflect eflect;

  public EflectMeter(Eflect eflect) {
    this.eflect = eflect;
  }

  @Override
  public void start() {
    this.eflect.start();
  }

  @Override
  public void stop() {
    this.eflect.stop();
  }

  @Override
  public double read() {
    double energy = 0;
    for (Virtualization virtualization : this.eflect.read()) {
      for (VirtualizedComponent component : virtualization.getVirtualizationList()) {
        if (component.getUnit() == VirtualizedComponent.Unit.ENERGY) {
          energy += component.getValue();
        }
      }
    }
    return energy;
  }
}
