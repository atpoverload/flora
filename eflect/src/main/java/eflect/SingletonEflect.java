package eflect;

import static java.util.concurrent.Executors.newScheduledThreadPool;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/** A singleton local eflect that can be used for convenience. */
public final class SingletonEflect implements Eflect {
  private static final long DEFAULT_PERIOD_MS = 10;
  private static final Logger logger = LoggerUtil.getLogger();
  private static final AtomicInteger counter = new AtomicInteger();
  private static final ThreadFactory threadFactory =
      r -> {
        Thread t = new Thread(r, "eflect-" + counter.getAndIncrement());
        t.setDaemon(true);
        return t;
      };

  private static ScheduledExecutorService executor = newScheduledThreadPool(3, threadFactory);
  private static SingletonEflect instance;

  /** Creates an instance of the underlying class if it hasn't been created yet. */
  public static synchronized SingletonEflect getInstance() {
    if (instance == null) {
      logger.info("creating the singleton eflect");
      instance =
          new SingletonEflect(
              new LocalEflect(
                  Long.parseLong(
                      System.getProperty(
                          "eflect.period.default", Long.toString(DEFAULT_PERIOD_MS))),
                  executor));
    }
    return instance;
  }

  private final LocalEflect eflect;

  private SingletonEflect(LocalEflect eflect) {
    this.eflect = eflect;
  }

  /** Starts a collector with the default period. */
  @Override
  public void start() {
    this.eflect.start();
  }

  /** Stops the collection. */
  @Override
  public void stop() {
    this.eflect.stop();
  }

  /** Returns the data from the last session. */
  @Override
  public List<Virtualization> read() {
    return this.eflect.read();
  }
}
