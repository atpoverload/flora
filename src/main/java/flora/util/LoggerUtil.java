package flora.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/** Helper for logging. */
public final class LoggerUtil {
  private static final SimpleDateFormat dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a z");

  private static boolean isInitialized = false;

  public static synchronized Logger getLogger() {
    if (!isInitialized) {
      ConsoleHandler handler = new ConsoleHandler();
      handler.setFormatter(
          new Formatter() {
            @Override
            public String format(LogRecord record) {
              return String.join(
                  " ", makePrefix(record), record.getMessage(), System.lineSeparator());
            }
          });

      Logger logger = Logger.getLogger("flora");
      logger.setUseParentHandlers(false);

      for (Handler hdlr : logger.getHandlers()) {
        logger.removeHandler(hdlr);
      }
      logger.addHandler(handler);
      isInitialized = true;

      return logger;
    } else {
      return Logger.getLogger("flora");
    }
  }

  private static String makePrefix(LogRecord record) {
    return String.join(
        " ",
        String.format("[%s]", record.getLevel()),
        "flora",
        String.format("(%s)", dateFormatter.format(new Date(record.getMillis()))),
        "[" + Thread.currentThread().getName() + "]:");
  }

  private LoggerUtil() {}
}
