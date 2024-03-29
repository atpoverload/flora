package flora.meter.contrib;

import static java.util.concurrent.Executors.newScheduledThreadPool;

// import eflect.Eflect;
// import eflect.LocalEflect;
// import eflect.SingletonEflect;
// import eflect.Virtualization;
// import eflect.Virtualization.VirtualizedComponent;
import eflect.Eflect;
import eflect.data.EnergyFootprint;
import flora.Meter;
import java.util.concurrent.atomic.AtomicInteger;

public class EflectMeter implements Meter {
  private static final AtomicInteger counter = new AtomicInteger();

  public static EflectMeter newLocalMeter(long periodMillis) {
    return new EflectMeter(Eflect.getInstance());
        // new LocalEflect(
        //     periodMillis,
        //     newScheduledThreadPool(
        //         3,
        //         r -> {
        //           Thread t =
        //               new Thread(r, String.format("eflect-meter-%d", counter.getAndIncrement()));
        //           t.setDaemon(true);
        //           return t;
        //         })));
  }

  // public static EflectMeter singletonMeter() {
  //   return new EflectMeter(SingletonEflect.getInstance());
  // }

  private final Eflect eflect;

  public EflectMeter(Eflect eflect) {
    this.eflect = eflect;
  }

  @Override
  public void start() {
    eflect.start();
  }

  @Override
  public void stop() {
    eflect.stop();
  }

  @Override
  public double read() {
    double energy = 0;
    for (EnergyFootprint footprint : eflect.read()) {
          energy += footprint.energy;
    }
    // for (Virtualization virtualization : eflect.read()) {
    //   for (VirtualizedComponent component : virtualization.getVirtualizationList()) {
    //     // TODO: filtering out negatives here but maybe we need a better method
    //     if (component.getUnit() == VirtualizedComponent.Unit.ENERGY && component.getValue() > 0) {
    //       energy += component.getValue();
    //     }
    //   }
    // }
    return energy;
  }
}
