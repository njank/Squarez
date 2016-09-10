package util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class Util {
  public static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
  
  private static Properties properties;
    
  public static void changeLanguage() {
    properties = getProperties(Settings.getString("language"));
  }
      
  private static Properties getProperties(String language){
    Properties props = new Properties();
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try {
      props.load(classLoader.getResourceAsStream("res/localization/language_"+language+".properties"));
    } catch (NullPointerException ex){
      Log.error("language not found");
    } catch (IOException ex) {
      Log.error(ex.getMessage());
    }
    return props;
  }
  
  public static String getProperty(String key){
    return properties.getProperty(key);
  }
  
  public static void openWebpage(String url) {
    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
      try {
        desktop.browse(new URL(url).toURI());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static String readWebsiteLine(String url) {
    String line = null;
    try {
      URLConnection con = new URL(url).openConnection();
      BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));
      line = r.readLine();
      r.close();
    } catch (IOException e) {
      /* Client is offline or in proxy which blocks port */
    }
    return line;
  }

  public static String readLine(String filename) {
    String line = null;
    try {
      BufferedReader r = new BufferedReader(new FileReader(filename));
      line = r.readLine();
      r.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return line;
  }

  public static int rand(int lo, int hi) {
    return (int) (Math.random() * (hi - lo + 1) + lo);
  }
  
  public static void writeLine(String filename, String line, boolean append) {
     try {
      BufferedWriter w = new BufferedWriter(new FileWriter(filename, append));
      w.write(line);
      w.flush();
      w.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}