package flora.contrib.eflect;

import flora.util.LoggerUtil;
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
    LoggerUtil.getLogger().fine("starting eflect");
    eflect.start();
  }

  @Override
  public void stop() {
    LoggerUtil.getLogger().fine("stopping eflect");
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
    LoggerUtil.getLogger().fine("eflect reports %fJ");
    return energy;
  }
}
