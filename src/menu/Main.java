package menu;

import util.Settings;

public class Main {
  public static View v;
  public static Model m;

  public static void main(String[] args) {
    Settings.setUp();
    
    m = new Model();
    v = new View(m);
  }
}
