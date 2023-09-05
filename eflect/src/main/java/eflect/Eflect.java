package eflect;

import java.util.List;

/** An interface that collects virtualization data. */
public interface Eflect {
  /** Starts a collector with the default period. */
  void start();

  /** Stops the collection. */
  void stop();

  /** Returns the data from the last session. */
  List<Virtualization> read();
}
