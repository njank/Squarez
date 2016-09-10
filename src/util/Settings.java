package util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

public class Settings {
  private static final String SETTINGS_FILE = "settings.ini";
  private static final String SETTINGS_FILE_SEPERATOR = "=";
  
  public static final String DISPLAY_TITLE = "Squarez";
  public static final String GITHUB_USERPROJECT = "njank/Squarez";
  
  public static final String GITHUB_URL = "https://github.com/"+GITHUB_USERPROJECT;
  public static final String GITHUB_RAW_URL = "https://raw.githubusercontent.com/"+GITHUB_USERPROJECT+"/master";
  
  private static final int[][] DISPLAY_RESOLUTIONS = new int[][]{
    new int[]{640, 360},
    new int[]{720, 405},
    new int[]{960, 540},
    new int[]{1024, 576},
    new int[]{1280, 720},
    new int[]{1366, 768},
    new int[]{1600, 900},
    new int[]{1920, 1080},
    new int[]{2048, 1152},
    new int[]{2560, 1440},
    new int[]{2880, 1620},
    new int[]{3200, 1800},
    new int[]{3840, 2160},
    new int[]{4096, 2304},
    new int[]{5120, 2880},
    new int[]{7680, 4320},
    new int[]{15360, 8640}
  };
  
  public static final Object[][] BLA = new Object[][]{
    new Object[]{ "language", null }
  };
  
  public static void setUp() {
    readSettings();
  }

  public static Setting[] settingsNew = new Setting[]{
    new StringSetting("language", "en"),
    new BooleanSetting("hardcore", false),
    new BooleanSetting("show_hud", true),
    new StringSetting("log_level", "info"),
    new StringSetting("username", System.getProperty("user.name")),
    new IntegerSetting("sq_size", 60),
    new IntegerSetting("sq_grow", 3),
    new IntegerSetting("sq_points", 10),
    new IntegerSetting("sq_new_amount", 5),
    new IntegerSetting("lvl_start", 1),
    new IntegerSetting("lvl_max", 10),
    new IntegerSetting("lvl_next", 500),
    new DoubleSetting("lvl_fac", 1.4542),
    new BooleanSetting("fullscreen", false),
    new BooleanSetting("vsync", false),
    new IntegerSetting("display_width", 1280),
    new IntegerSetting("display_height", 720),
    new IntegerSetting("framerate", 60),
  };  
  
  public static void readSettings() {
    File f = new File(SETTINGS_FILE);
    if (!f.exists()) {
      resetSettings();
    } else {
      try {
        BufferedReader r = new BufferedReader(new FileReader(SETTINGS_FILE));
        String line = null;
        while ((line = r.readLine()) != null) {
          SimpleEntry<String, String> parsed = parseLine(line);
          if(parsed != null) {
            parseSettingValue(parsed.getKey(), parsed.getValue());
          }
        }
        r.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      Log.info("read settings from file system");
      writeSettings();
    }
  }
  
  public static void writeSettings() {
    try {
      BufferedWriter w = new BufferedWriter(new FileWriter(SETTINGS_FILE, false));
      String output = "";
      for(Setting s : settingsNew) {
        output += s.toString(SETTINGS_FILE_SEPERATOR) + System.lineSeparator();
      }
      w.write(output);
      w.flush();
      w.close();
      Log.info("wrote settings to file system");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private static SimpleEntry<String, String> parseLine(String line){
    if(line.matches("^[a-zA-Z_]+"+SETTINGS_FILE_SEPERATOR+".+$")){ // format: <KEY><SETTINGS_FILE_SEPERATOR><VALUE>
      String[] exploded = line.split(SETTINGS_FILE_SEPERATOR);
      return new SimpleEntry<>(exploded[0], exploded[1].replaceAll(" .*", "")); // remove every behind space
    }
    return null;
  }

  public static void resetSettings() {
    for(Setting setting : settingsNew){
      setting.setDefaultValue();
    }
    writeSettings();
  }
  
  public static String[] getResolutions(){
    Dimension display = Toolkit.getDefaultToolkit().getScreenSize();
    LinkedList<String> returnRes = new LinkedList<String>();
    for(int[]res : DISPLAY_RESOLUTIONS){
      if(res[0]<display.width){ // show every 16:9 resolution smaller than actual screen size
        returnRes.add(res[0]+"x"+res[1]);
      }else break;
    }
    return returnRes.toArray(new String[0]);
  }
  
  public static void parseSettingValue(String key, String value){
    for(Setting setting : settingsNew){
      if(setting.getKey().equals(key)){
        setting.parseValue(value);
        Log.debug("parseSetting("+key+", "+value+")");
        return;
      }
    }
  }
  
  public static void setSettingValue(String key, Object value){
    for(Setting setting : settingsNew){
      if(setting.getKey().equals(key)){
        setting.setValue(value);
        Log.debug("setSetting("+key+", "+value+")");
        return;
      }
    }
  }
  
  private static Object getSettingValue(String key){
    for(Setting setting : settingsNew){
      if(setting.getKey().equals(key)){
        return setting.getValue();
      }
    }
    return null;
  }
  
  public static String getString(String key){
    return getSettingValue(key)+"";
  }
  
  public static int getInt(String key){
    return ((Integer)getSettingValue(key)).intValue();
  }
  
  public static double getDouble(String key){
    return ((Double)getSettingValue(key)).doubleValue();
  }
  
  public static boolean getBoolean(String key){
    return ((Boolean)getSettingValue(key)).booleanValue();
  }
}

abstract class Setting {
  private String key;
  private Object defaultValue;
  protected Object value;

  protected Setting(String key, Object defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;
    setDefaultValue();
  }
  
  protected Setting(String key, Object defaultValue, Object value) {
    this.key = key;
    this.defaultValue = defaultValue;
    this.value = value;
  }
  
  public void setDefaultValue() {
    this.value = this.defaultValue;
  }
  
  public String toString(String seperator) {
    return new String(key+seperator+value);
  }

  public String getKey() {
    return key;
  }
  
  public abstract Object getValue();
  public abstract void parseValue(String value);
  public abstract void setValue(Object value);
}
  
class StringSetting extends Setting {
  public StringSetting(String key, String defaultValue) {
    super(key, defaultValue);
  }
  public String getValue() {
    return (String)value;
  }
  public void parseValue(String value) {
    super.value = value;
  }
  public void setValue(Object value) {
    super.value = (String)value;
  }
}

class BooleanSetting extends Setting {
  public BooleanSetting(String key, Boolean defaultValue) {
    super(key, defaultValue);
  }
  public Boolean getValue() {
    return (Boolean)value;
  }
  public void parseValue(String value) {
    super.value = Boolean.parseBoolean(value);
  }
  public void setValue(Object value) {
    super.value = (Boolean)value;
  }
}

class IntegerSetting extends Setting {
  public IntegerSetting(String key, Integer defaultValue) {
    super(key, defaultValue);
  }
  public Integer getValue() {
    return (Integer)value;
  }
  public void parseValue(String value) {
    try{
      super.value = Integer.parseInt(value);
    }catch(NumberFormatException ex){
      super.setDefaultValue();
    }
  }
  public void setValue(Object value) {
    super.value = (Integer)value;
  }
}

class DoubleSetting extends Setting {
  public DoubleSetting(String key, Double defaultValue) {
    super(key, defaultValue);
  }
  public Double getValue() {
    return (Double)value;
  }
  public void parseValue(String value) {
    try{
      super.value = Double.parseDouble(value);
    }catch(NumberFormatException ex){
      super.setDefaultValue();
    }
  }
  public void setValue(Object value) {
    super.value = (Double)value;
  }
}