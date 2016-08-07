package util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
  private static Logger logger;
  private static final Level LEVEL = Level.ALL;

  static {
    System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$.1s %5$s%6$s%n");
    logger = Logger.getLogger("log");
  }
  
  public static void logError(String message){
    logger.log(Level.SEVERE, message);
  }
  
  public static void logWarning(String message){
    logger.log(Level.WARNING, message);
  }
  
  public static void logInfo(String message){
    logger.log(Level.INFO, message);
  }
  
  public static void logDebug(String message){
    logger.log(Level.INFO, message);
  }
}