package flora;

/** An interface that returns data measured over a metering session. */
public interface Meter {
  /** Starts running the meter. */
  void start();

  /** Stops running the meter. */
  void stop();

  /** Reads the value from the last metering session. */
  double read();
}
