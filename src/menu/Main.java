package menu;

public class Main {
  public static View v;
  public static Model m;

  public static void main(String[] args) {
    m = new Model();
    v = new View(m);
    m.setView(v);
  }
}
