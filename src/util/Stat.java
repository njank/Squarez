package util;

public class Stat
        implements Comparable {

    public int score;
    public int level;
    public long time;
    public long date;
    public boolean usedKinect;

    public Stat(String csv) {
        String[] values = csv.split(";");
        try {
            this.score = Integer.parseInt(values[0]);
            this.level = Integer.parseInt(values[1]);
            this.time = Long.parseLong(values[2]);
            this.date = Long.parseLong(values[3]);
            this.usedKinect = Boolean.parseBoolean(values[4]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public int compareTo(Object o) {
        return ((Stat) o).score - this.score;
    }
}
