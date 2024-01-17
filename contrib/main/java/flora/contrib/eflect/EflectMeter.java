package flora.contrib.eflect;

import eflect.Eflect;
import eflect.data.EnergyFootprint;
import flora.Meter;

public class EflectMeter implements Meter {
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
      if (footprint.energy > 0) {
        energy += footprint.energy;
      }
    }
    return energy;
  }
}
