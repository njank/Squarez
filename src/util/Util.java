package util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class Util {
  public static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      
  public static Properties getProperties(){
    Properties props = new Properties();
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      props.load(classLoader.getResourceAsStream("res/localization/language_"+Settings.language+".properties"));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return props;
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
  
  
  
  // 2do
  //  static void writeStats() {
  //    try {
  //      BufferedWriter w = new BufferedWriter(new FileWriter("stats.csv", true));
  //      w.write(Logic.score + ";" + Logic.level + ";" + Logic.time + ";" + System.currentTimeMillis() + ";false\n");
  //      w.flush();
  //      w.close();
  //    } catch (IOException e) {
  //      e.printStackTrace();
  //    }
  //  }
  //
  //  static String[] loadStats() {
  //    LinkedList<Stat> statsAll = new LinkedList();
  //    Logic.stats = new String[3];
  //    try {
  //      BufferedReader r = new BufferedReader(new FileReader("stats.csv"));
  //      String line = null;
  //      while ((line = r.readLine()) != null) {
  //        statsAll.add(new Stat(line));
  //      }
  //      r.close();
  //    } catch (IOException e) {
  //      e.printStackTrace();
  //      return null;
  //    }
  //    SimpleDateFormat dateFormat = new SimpleDateFormat();
  //    dateFormat.applyPattern("yyyy-MM-dd");
  //    SimpleDateFormat timeFormat = new SimpleDateFormat();
  //    timeFormat.applyPattern("mm:ss");
  //    Collections.sort(statsAll);
  //    ListIterator it = statsAll.listIterator();
  //    int i = 0;
  //    while (i < Logic.stats.length) {
  //      if (it.hasNext()) {
  //        Stat s = (Stat) it.next();
  //        Logic.stats[(i++)] = ("  " + s.score + "   " + timeFormat.format(Long.valueOf(s.time)) + "   " + dateFormat.format(Long.valueOf(s.date)) + "   " + (s.usedKinect ? "*" : ""));
  //      } else {
  //        Logic.stats[(i++)] = "-";
  //      }
  //    }
  //    return Logic.stats;
  //  }
}
