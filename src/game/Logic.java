package game;

import util.Settings;
import util.Square;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logic {
  public static final int VIRTUAL_WIDTH = 1600;
  public static final int VIRTUAL_HEIGHT = 900;
  public static final int TITLE_FONT_MIN_LAYER = 1;
  public static final int TITLE_FONT_MAX_LAYER = 4;
  public static final int TITLE_FONT_MARGIN = 1;
  
  public static Square mySquare;
  public static Square[] squarez;
  private static State state;
  private static String[] stats;
  //private static Clip menuMusic;
  //private static Clip ingameMusic;
  //private static Font titleFont;
  //private static Font textFont;
  private static int titleFontLayer;
  private static boolean titleFontLayerIncr;
  
  // statistic related parameter
  public static int score;
  public static int level;
  private static int nextLevel;
  private static long levelStartTime;
  private static long levelEndTime;
  private static int direction;
  public static int largestSize = 0;
  
  public static boolean quit = false;

  public static enum State {
      START, INGAME, PAUSE, END;
  }
  
  public static void setUp() {
    // if fullscreen is requested set screen size to the actual resolution
    if(Settings.fullscreen) {
      Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
      Settings.display_width = d.width;
      Settings.display_height = d.height;
    }
    initStartParameter();
    initFonts();
    initSound();
    resetAll();
    //initStartMenuSquarez();
    //menuMusic.loop(-1);
  }
  
  public static void update() {
    moveMouse();
    moveSquarez();
    addSquarez();
    checkIntersection();
    checkLevel();
  }
  
  public static void resetAll() {
    score = 0;
    level = Settings.lvl_start;
    nextLevel = Settings.lvl_next;
    mySquare.size = Settings.sq_size;
    squarez = new Square[Settings.sq_new_amount * 6];
    direction = 1;
    levelStartTime = System.currentTimeMillis();
    //menuMusic.stop();
  }

  public static void moveSquarez() {
    for (int i = 0; i < squarez.length; i++) {
      if ((squarez[i] != null)
              && (!squarez[i].move(level, VIRTUAL_WIDTH, VIRTUAL_HEIGHT))) {
        squarez[i] = null;
      }
    }
  }

  public static void addSquarez() {
    if (rand(1, 550 / ((direction % 2 == 1 ? VIRTUAL_WIDTH : VIRTUAL_HEIGHT) * Settings.sq_new_amount / Settings.sq_size)) == 1) {
      int newDirection = Settings.hardcore ? rand(1, 4) : direction;
      int spawnX = 0;
      int spawnY = 0;
      switch (newDirection) {
        case 1:
          spawnX = rand(0, VIRTUAL_WIDTH - Settings.sq_size);
          spawnY = -Settings.sq_size;
          break;
        case 2:
          spawnX = VIRTUAL_WIDTH + Settings.sq_size;
          spawnY = rand(0, VIRTUAL_HEIGHT - Settings.sq_size);
          break;
        case 3:
          spawnX = rand(0, VIRTUAL_WIDTH - Settings.sq_size);
          spawnY = VIRTUAL_HEIGHT + Settings.sq_size;
          break;
        case 4:
          spawnX = -Settings.sq_size;
          spawnY = rand(0, VIRTUAL_HEIGHT - Settings.sq_size);
      }
      int type = rand(1, 1000);
      type = ((type == 666) ? 5 : ((type <= (100 / level) + 100) ? 2 : ((type <= 300) ? 3 : ((type <= 300 + 10 * level) ? 4 : 1))));
      addSquare(new Square(spawnX, spawnY, type, Settings.sq_size, newDirection));
    }
  }

  private static void addSquare(Square newSquare) {
    for (int i = 0; i < squarez.length; i++) {
      if (squarez[i] == null) {
        squarez[i] = newSquare;
        return;
      }
    }
  }

  private static int rand(int lo, int hi) {
    return (int) (Math.random() * (hi - lo + 1) + lo);
  }

  public static void moveMouse() {
    mySquare.x = (Controller.mouseX - mySquare.size / 2);
    mySquare.y = (Controller.mouseY - mySquare.size / 2);
  }

  public static void checkIntersection() {
    for (int i = 0; i < squarez.length; i++) {
      if ((squarez[i] != null) && (mySquare.intersects(squarez[i]))) {
        switch (squarez[i].type) {
          case 1: // blue
            score += Settings.sq_points;
            mySquare.grow(Settings.sq_grow);
            break;
          case 2: // green
            if(mySquare.size > largestSize)
              largestSize = mySquare.size;
            mySquare.x += (mySquare.size - Settings.sq_size) / 2;
            mySquare.y += (mySquare.size - Settings.sq_size) / 2;
            mySquare.size = Settings.sq_size;
            break;
          case 3: // black
            if(mySquare.size > largestSize)
              largestSize = mySquare.size;
            levelEndTime = (System.currentTimeMillis() - levelStartTime);
            state = State.END;
            //game.pause();
                    resetAll();
            //ingameMusic.stop();
            //menuMusic.loop(-1);
            //FileHandler.writeStats();
            //FileHandler.loadStats();
            largestSize = 0;
            return;
          case 4: // orange
            score += Settings.sq_points * 10;
            changeDirection();
            break;
          case 5: // magenta
            score += Settings.sq_points * 100;
            mySquare.grow(Settings.sq_grow * 10);
            squarez = new Square[30];
        }
        squarez[i] = null;
      }
    }
  }

  private static void changeDirection() {
    for (int i = 0; i < squarez.length; i++) {
      if (squarez[i] != null) {
        squarez[i].changeDirection();
      }
    }
    if (!Settings.hardcore) {
      direction = (direction % 4 + 1);
    }
  }

  public static void levelUp() {
    if (level != Settings.lvl_max) {
      level += 1;
      nextLevel = ((int) (nextLevel * Settings.lvl_fac));
    }
  }

  public static void levelDown() {
    if (level != 1) {
      level -= 1;
      nextLevel = ((int) (nextLevel / Settings.lvl_fac));
    }
  }

  public static void pause() {
    if (state == State.INGAME) {
      state = State.PAUSE;
            //game.pause();
      //ingameMusic.stop();
      //menuMusic.loop(-1);
    } else if (state == State.PAUSE) {
      state = State.INGAME;
            //game.unpause();
      //menuMusic.stop();
      //ingameMusic.loop(-1);
    }
  }

  public static void checkLevel() {
    while (score > nextLevel) {
      levelUp();
    }
  }

  public static void updateTitleFontLayer() {
    if (titleFontLayerIncr) {
      if (++titleFontLayer == 4) {
        titleFontLayerIncr = false;
      }
    } else if (--titleFontLayer == 1) {
      titleFontLayerIncr = true;
    }
  }

  private static void initStartParameter() {
    state = State.START;
    mySquare = new Square(-Settings.sq_size, -Settings.sq_size, 0, Settings.sq_size, 0);
    //this(game = new Game(this, view)).start();
    titleFontLayer = 1;
    titleFontLayerIncr = true;
    nextLevel = Settings.lvl_next;
    stats = null;
  }

  public static void initFonts() {
    try {
//            titleFont=Font.createFont(Font.TRUETYPE_FONT,
//                    getClass().getResourceAsStream("fonts/fipps.otf")).deriveFont((float)DISPLAY_WIDTH/8);
//            textFont=Font.createFont(Font.TRUETYPE_FONT,
//                    getClass().getResourceAsStream("fonts/monaco.ttf")).deriveFont((float)(DISPLAY_WIDTH/20));
    } catch (Exception e) { /* Fonts not found */ }
  }

  public static void initSound() {
    try {
//            menuMusic=AudioSystem.getClip();
//            menuMusic.open(AudioSystem.getAudioInputStream(getClass().getResource("sound/menu.wav")));
//            ingameMusic=AudioSystem.getClip();
//            ingameMusic.open(AudioSystem.getAudioInputStream(getClass().getResource("sound/ingame.wav")));
        } catch (Exception e) { /* Sound not found */ }
    }

  private static void initStartMenuSquarez() {
    squarez = new Square[20];
    for (int i = 0; i < squarez.length; i++) {
        int rand = rand(1, 1000);
        Square newSquare = new Square(rand(0, VIRTUAL_WIDTH - Settings.sq_size), rand(0, VIRTUAL_HEIGHT - Settings.sq_size), rand == 666 ? 5 : rand <= 310 ? 4 : rand <= 300 ? 3 : rand <= 100 ? 2 : 1, Settings.sq_size, 0);

        boolean intersects = false;
        for (int j = 0; j < squarez.length; j++) {
            if (squarez[j] != null) {
                intersects |= newSquare.intersects(squarez[j]);
            }
        }
        if (intersects) {
            i--;
        } else {
            squarez[i] = newSquare;
        }
    }
  }
  
  public static String getTime() {
    Date date = new Date(System.currentTimeMillis() - Logic.levelStartTime);
    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS", Locale.US);
    return sdf.format(date);
  }
}