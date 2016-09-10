package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class Log {
  private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final Level DEFAULT_LOG_LEVEL = Level.INFO; // also the starting level before reading settings
  
  private static Level level = DEFAULT_LOG_LEVEL;
  
  private static Level[] logLevels = new Level[]{
    Level.SEVERE, Level.WARNING, Level.INFO, Level.FINE
  };
  
  public static String[] logLevelsStrings = new String[]{
    "error", "warning", "info", "debug"
  };
  
  public static void setLogLevel(String logLevel){
    for(int i=0; i<logLevel.length(); i++){
      if(logLevel.equals(logLevelsStrings[i])){
        level = logLevels[i];
        return;
      }
    }
    level = DEFAULT_LOG_LEVEL;
  }

  public static void log(String message, Level level){
    if(level.intValue() >= Log.level.intValue()){
      System.out.println(level.toString().substring(0,1)+" "+LOG_DATE_FORMAT.format(new Date())+" "+message);
    }
  }
  
  public static void error(String message){
    log(message, Level.SEVERE);
  }
  
  public static void warning(String message){
    log(message, Level.WARNING);
  }
  
  public static void info(String message){
    log(message, Level.INFO);
  }
  
  public static void debug(String message){
    log(message, Level.FINE);
  }
}