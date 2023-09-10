package flora.examples.toggle;

/** Configuration for the {@link ToggleMachine}. */
record ToggleConfiguration(boolean toggle1, boolean toggle2)
    implements Comparable<ToggleConfiguration> {
  @Override
  public int compareTo(ToggleConfiguration other) {
    int firstCompare = Boolean.compare(toggle1, other.toggle1);
    switch (firstCompare) {
      case 0:
        return Boolean.compare(toggle2, other.toggle2);
      default:
        return firstCompare;
    }
  }
}
