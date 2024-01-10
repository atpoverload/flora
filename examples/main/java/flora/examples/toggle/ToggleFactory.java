package flora.examples.toggle;

import flora.work.EncodedWorkFactory;

public class ToggleFactory implements EncodedWorkFactory<ToggleKnobs, ToggleConfiguration, Toggle> {
  @Override
  public ToggleKnobs knobs() {
    return ToggleKnobs.INSTANCE;
  }

  @Override
  public int knobCount() {
    return 2;
  }

  @Override
  public int configurationCount(int knob) {
    return 2;
  }

  @Override
  public Toggle newWorkUnit(int[] configuration) {
    return Toggle.newFromArray(configuration);
  }

  @Override
  public boolean isValidConfiguration(int[] configuration) {
    return (configuration[0] == 0 || configuration[0] == 1)
        && (configuration[1] == 0 || configuration[1] == 1);
  }

  @Override
  public int[] repairConfiguration(int[] configuration) {
    return new int[] {Math.abs(configuration[0]) % 1, Math.abs(configuration[1]) % 1};
  }

  @Override
  public int[] randomConfiguration() {
    return Toggle.randomConfiguration().configuration().asArray();
  }
}
