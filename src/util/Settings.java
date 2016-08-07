package util;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map.Entry;

public class Settings {
  public static final String GITHUB_URL = "https://github.com/njank/Squarez";
  public static final String GITHUB_RAW_URL = "https://raw.githubusercontent.com/njank/Squarez/master";
  public static final String DISPLAY_TITLE = "Squarez";
  
  private static final String SETTINGS_FILE = "settings.ini";
  private static final String SETTINGS_FILE_SEPERATOR = "=";
  
  public static final Dimension[][] DISPLAY_RESOLUTIONS = new Dimension[][]{
    /*  4:3  */ new Dimension[]{new Dimension(1600,1200), new Dimension(1024,768), new Dimension(800,600)},
    /* 16:10 */ new Dimension[]{new Dimension(1920,1200), new Dimension(1680,1050), new Dimension(1440,900), new Dimension(1280,800)},
    /* 16:9  */ new Dimension[]{new Dimension(2560,1440), new Dimension(1920,1080), new Dimension(1600,900), new Dimension(1280,720), new Dimension(1366,768), new Dimension(1536,864)}
  };
  
  // general settings default constants
  private static final String DEFAULT_LANGUAGE = "en";
  private static final boolean DEFAULT_HARDCORE = false;
  private static final boolean DEFAULT_CHEATS = false;
  
  // logic settings default constants
  private static final int DEFAULT_SQ_GROW = 3;
  private static final int DEFAULT_SQ_POINTS = 10;
  private static final int DEFAULT_SQ_NEW_AMOUNT = 5;  
  private static final int DEFAULT_SQ_SIZE = 60;
  private static final int DEFAULT_LVL_START = 1;
  private static final int DEFAULT_LVL_NEXT = 500;
  private static final int DEFAULT_LVL_MAX = 10;
  private static final double DEFAULT_LVL_FAC = 1.4542;
  
  // display settings default constants
  private static final boolean DEFAULT_FULLSCREEN = false;
  private static final int DEFAULT_DISPLAY_WIDTH = 1280;
  private static final int DEFAULT_DISPLAY_HEIGHT = 720;
  private static final int DEFAULT_FRAMERATE = 60;
  private static final boolean DEFAULT_VSYNC = false;
  
  
  // general settings
  public static String language;
  public static boolean hardcore;
  public static boolean cheats;
  
  // logic settings
  public static int sq_size;
  public static int sq_grow;
  public static int sq_new_amount;
  public static int sq_points;
  public static int lvl_max;
  public static int lvl_next;
  public static double lvl_fac;
  public static int lvl_start;
  
  // display settings
  public static int display_width;
  public static int display_height;
  public static boolean fullscreen;
  public static boolean vsync;
  public static int framerate;
  
  static {
    readSettings();
  }
  
  public static void readSettings() {
    HashMap<String, Object> settings = new HashMap<>();
    
    File f = new File(SETTINGS_FILE);
    if (!f.exists()) {
      getDefaultSettings(settings);
      saveSettingsFile(settings);
    } else {
      try {
        BufferedReader r = new BufferedReader(new FileReader(SETTINGS_FILE));
        String line = null;
        while ((line = r.readLine()) != null) {
          SimpleEntry<String, String> parsed = parseLine(line);
          if(parsed != null) {
            settings.put(parsed.getKey(), parsed.getValue());
          }
        }
        r.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    Log.logInfo("read settings file");
    loadSettings(settings);
  }

  public static void loadSettings(HashMap<String, Object> settings) {
    for(Entry<String, Object> entry : settings.entrySet()) {
      String value = entry.getValue()+"";
      Log.logDebug("read "+entry.getKey()+SETTINGS_FILE_SEPERATOR+value);
      switch(entry.getKey()){
        case "hardcore": hardcore = validateBoolean(value, DEFAULT_HARDCORE); break;
        case "cheats": cheats = validateBoolean(value, DEFAULT_CHEATS); break;
        case "sq_size": sq_size = validateInteger(value, DEFAULT_SQ_SIZE); break;
        case "sq_grow": sq_grow = validateInteger(value, DEFAULT_SQ_GROW); break;
        case "sq_points": sq_points = validateInteger(value, DEFAULT_SQ_POINTS); break;
        case "sq_new_amount": sq_new_amount = validateInteger(value, DEFAULT_SQ_NEW_AMOUNT); break;
        case "lvl_max": lvl_max = validateInteger(value, DEFAULT_LVL_MAX); break;
        case "lvl_next": lvl_next = validateInteger(value, DEFAULT_LVL_NEXT); break;
        case "lvl_start": lvl_start = validateInteger(value, DEFAULT_LVL_START); break;
        case "lvl_fac": lvl_fac = validateDouble(value, DEFAULT_LVL_FAC); break;
        case "fullscreen": fullscreen = validateBoolean(value, DEFAULT_FULLSCREEN); break;
        case "vsync": vsync = validateBoolean(value, DEFAULT_VSYNC); break;
        case "display_width": display_width = validateInteger(value, DEFAULT_DISPLAY_WIDTH); break;
        case "display_height": display_height = validateInteger(value, DEFAULT_DISPLAY_HEIGHT); break;
        case "framerate": framerate = validateInteger(value, DEFAULT_FRAMERATE); break;
        case "language":
          String tempLanguage = value.toLowerCase();
          try {
            InputStream inp = Util.classLoader.getResourceAsStream("res/localization/language_"+tempLanguage+".properties");
            new BufferedReader(new InputStreamReader(inp));
            language = tempLanguage; // if file exists it reaches this line
          } catch (Exception ex){
            language = DEFAULT_LANGUAGE; // if file does not exist keep it default
          }
      }
    }
    Log.logInfo("loaded settings");
  }
  
  private static int validateInteger(String value, int defaultValue){
    try{
      return Integer.parseInt(value);
    }catch(NumberFormatException ex){
      return defaultValue;
    }
  }
  
  private static boolean validateBoolean(String value, boolean defaultValue){
    try{
      return Boolean.parseBoolean(value);
    }catch(NumberFormatException ex){
      return defaultValue;
    }
  }
  
  private static double validateDouble(String value, double defaultValue){
    try{
      return Double.parseDouble(value);
    }catch(NumberFormatException ex){
      return defaultValue;
    }
  }

  public static void saveSettingsFile(HashMap<String, Object> settings) {
    try {
      BufferedWriter w = new BufferedWriter(new FileWriter(SETTINGS_FILE, true));
      String s = "";
      for(Entry<String, Object> entry : settings.entrySet()) {
        s += entry.getKey()+SETTINGS_FILE_SEPERATOR+entry.getValue()+"\r\n";
      }
      w.write(s);
      w.flush();
      w.close();
      Log.logInfo("created settings file");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private static SimpleEntry<String, String> parseLine(String line){
    if(line.matches("^[a-zA-Z_]+"+SETTINGS_FILE_SEPERATOR+".+$")){ // format: <KEY>\t<VALUE>
      String[] exploded = line.split(SETTINGS_FILE_SEPERATOR);
      return new SimpleEntry<>(exploded[0], exploded[1].replaceAll(" .*", "")); // remove every behind space
    }
    return null;
  }

  private static HashMap getDefaultSettings(HashMap<String, Object> settings) {
    settings.put("language", DEFAULT_LANGUAGE);
    settings.put("hardcore", DEFAULT_HARDCORE+"");
    settings.put("cheats", DEFAULT_CHEATS+"");
    settings.put("sq_size", DEFAULT_SQ_SIZE+"");
    settings.put("sq_grow", DEFAULT_SQ_GROW+"");
    settings.put("sq_points", DEFAULT_SQ_POINTS+"");
    settings.put("sq_new_amount", DEFAULT_SQ_NEW_AMOUNT+"");
    settings.put("lvl_start", DEFAULT_LVL_START+"");
    settings.put("lvl_max", DEFAULT_LVL_MAX+"");
    settings.put("lvl_next", DEFAULT_LVL_NEXT+"");
    settings.put("lvl_fac", DEFAULT_LVL_FAC+"");
    settings.put("fullscreen", DEFAULT_FULLSCREEN+"");
    settings.put("vsync", DEFAULT_VSYNC+"");
    settings.put("display_width", DEFAULT_DISPLAY_WIDTH+"");
    settings.put("display_height", DEFAULT_DISPLAY_HEIGHT+"");
    settings.put("framerate", DEFAULT_FRAMERATE+"");
    
    return settings;
  }
}