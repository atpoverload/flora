package eflect.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/** Helper for logging. */
public final class LoggerUtil {
  private static final String NAME = "eflect";
  private static final SimpleDateFormat DATE_FORMATTER =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a z");

  private static boolean IS_INITIALIZED = false;

  public static synchronized Logger getLogger() {
    if (!IS_INITIALIZED) {
      ConsoleHandler handler = new ConsoleHandler();
      handler.setFormatter(
          new Formatter() {
            @Override
            public String format(LogRecord record) {
              return String.join(
                  " ", makePrefix(record), record.getMessage(), System.lineSeparator());
            }
          });
          handler.setLevel(Level.FINE);

      Logger logger = Logger.getLogger(NAME);
      logger.setUseParentHandlers(false);

      for (Handler hdlr : logger.getHandlers()) {
        logger.removeHandler(hdlr);
      }
      logger.addHandler(handler);
      IS_INITIALIZED = true;

      return logger;
    } else {
      return Logger.getLogger(NAME);
    }
  }

  private static String makePrefix(LogRecord record) {
    return String.join(
        " ",
        String.format("[%s]", record.getLevel()),
        NAME,
        String.format("(%s)", DATE_FORMATTER.format(new Date(record.getMillis()))),
        "[" + Thread.currentThread().getName() + "]:");
  }

  private LoggerUtil() {}
}
