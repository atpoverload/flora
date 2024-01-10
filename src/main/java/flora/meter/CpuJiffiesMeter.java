package flora.meter;

import static flora.util.LoggerUtil.getLogger;

import flora.Meter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

/** A {@link Meter} that records total system jiffies consumed. */
public class CpuJiffiesMeter implements Meter {
  private static final Logger logger = getLogger();
  private static final String PROC_STAT = "/proc/stat";

  private static final double parseJiffies(String jiffies) {
    String[] splitJiffies = jiffies.split(" ");
    // TODO: the first line has "cpu  " instead of "cpu0 ", so this got shifted
    return Double.parseDouble(splitJiffies[2])
        + Double.parseDouble(splitJiffies[3])
        + Double.parseDouble(splitJiffies[4])
        + Double.parseDouble(splitJiffies[6])
        + Double.parseDouble(splitJiffies[7]);
  }

  private boolean isRunning = false;
  private String start = "";
  private String end = "";

  /** Grabs the current cpu jiffies. */
  @Override
  public void start() {
    if (isRunning) {
      logger.fine(
          String.format("ignoring start for %s while running", this.getClass().getSimpleName()));
      return;
    }
    isRunning = true;
    start = "";
    end = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(PROC_STAT));
      start = reader.readLine();
      reader.close();
    } catch (IOException e) {
      logger.fine(String.format("resetting %s due to %s", this.getClass().getSimpleName(), e));
      start = "";
      isRunning = false;
    }
  }

  /** Sets the elapsed time between the last start and now. */
  @Override
  public void stop() {
    if (!isRunning) {
      logger.fine(
          String.format("ignoring stop for %s while stopped", this.getClass().getSimpleName()));
      return;
    }
    try {
      BufferedReader reader = new BufferedReader(new FileReader(PROC_STAT));
      end = reader.readLine();
      reader.close();
    } catch (IOException e) {
      logger.fine(String.format("resetting %s due to %s", this.getClass().getSimpleName(), e));
      start = "";
    }
    isRunning = false;
  }

  /** Returns the stored elapsed time as seconds with nano precision. */
  @Override
  public double read() {
    if (start.isBlank() && end.isBlank()) {
      logger.fine(String.format("reading unused %s returns 0", this.getClass().getSimpleName()));
      return 0;
    } else if (end.isBlank()) {
      logger.fine(
          String.format(
              "reading while running %s returns current", this.getClass().getSimpleName()));
      try {
        BufferedReader reader = new BufferedReader(new FileReader(PROC_STAT));
        String now = reader.readLine();
        reader.close();
        return parseJiffies(now) - parseJiffies(start);
      } catch (IOException e) {
        logger.fine(String.format("returning 0 due to %s", e));
        return 0;
      }
    }
    return parseJiffies(end) - parseJiffies(start);
  }
}
