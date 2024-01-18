package flora.contrib.eflect;

import eflect.Eflect;
import eflect.data.EnergyFootprint;
import eflect.util.TimeUtil;
import flora.Meter;
import flora.util.LoggerUtil;
import java.time.Duration;
import java.time.Instant;

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
    Instant start = Instant.MAX;
    Instant end = Instant.MIN;
    for (EnergyFootprint footprint : eflect.read()) {
      start = TimeUtil.min(footprint.start, start);
      end = TimeUtil.min(footprint.end, end);
      if (footprint.energy > 0) {
        energy += footprint.energy;
      }
    }
    LoggerUtil.getLogger()
        .fine(
            String.format(
                "eflect reports %fJ over %s to %s (%s)",
                energy, start, end, Duration.between(start, end)));
    return energy;
  }
}
