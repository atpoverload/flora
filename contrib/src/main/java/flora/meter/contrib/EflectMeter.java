package flora.meter.contrib;

import eflect.Eflect;
import eflect.SingletonEflect;
import eflect.Virtualization;
import eflect.Virtualization.VirtualizedComponent;
import flora.Meter;

public class EflectMeter implements Meter {
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
