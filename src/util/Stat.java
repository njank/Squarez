package util;

public class Stat implements Comparable{
  private String userName;
  private int score;
  private int largestSize;
  private long time;
  private long date;

  public Stat(String userName, int score, int largestSize, long time, long date) {
    this.userName = userName;
    this.score = score;
    this.largestSize = largestSize;
    this.time = time;
    this.date = date;
  }

  public Stat(String csv) {
    String[] values = csv.split(";");
    try {
      this.userName = values[0];
      this.score = Integer.parseInt(values[1]);
      this.largestSize = Integer.parseInt(values[2]);
      this.time = Long.parseLong(values[3]);
      this.date = Long.parseLong(values[4]);
    } catch (NumberFormatException e) {
        e.printStackTrace();
    }
  }

  public int compareTo(Object o) {
      return ((Stat) o).score - this.score;
  }
  
  public String toString(){
    return userName+";"+score+";"+largestSize+";"+time+";"+date;
  }
}