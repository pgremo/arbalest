package arbalest;

import java.util.logging.*;

import static arbalest.Configuration.getProperties;
import static arbalest.Symbol.logLevel;
import static java.util.Optional.ofNullable;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

public class LoggingConfiguration {
  public LoggingConfiguration() {
    Level level = ofNullable((String) logLevel.get(getProperties())).map(Level::parse).orElse(INFO);

    Handler handler = new ConsoleHandler();
    handler.setLevel(level);
    handler.setFormatter(new SimpleFormatter());

    Logger logger = getLogger(Arbalest.class.getPackage().getName());
    logger.setLevel(level);
    logger.addHandler(handler);
  }
}
