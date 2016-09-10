package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import static util.Settings.resetSettings;

public class Stats {
  private static LinkedList<Stat> stats;
  private static final String SAVE_FILE = "saves.csv";
  
  public static void save(Stat newStat){
    stats.add(newStat);
    Util.writeLine(SAVE_FILE, newStat.toString() + System.lineSeparator(), true);
    Log.debug("saved stats file");
  }
  
  public static void load(){
    stats = new LinkedList<Stat>();
    try {
      File f = new File(SAVE_FILE);
      if (!f.exists()) {
        f.createNewFile();
      } else {
        BufferedReader r = new BufferedReader(new FileReader(SAVE_FILE));
        String line = null;
        while ((line = r.readLine()) != null) {
          stats.add(new Stat(line));
        }
        r.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    Log.debug("loaded stats file");
  }
}