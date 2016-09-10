package game;

import util.Settings;
import util.Square;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import util.Log;
import util.Square.Type;
import util.Stat;
import util.Stats;
import util.Util;

public class Logic {
  public static enum State {
    START, INGAME, PAUSE, END;
  }
  
  public static final int VIRTUAL_WIDTH = 1600;
  public static final int VIRTUAL_HEIGHT = 900;
  public static final int TITLE_FONT_MIN_LAYER = 1;
  public static final int TITLE_FONT_MAX_LAYER = 4;
  public static final int TITLE_FONT_MARGIN = 1;
  
  public static Square mySquare = null;
  public static Square[] squarez;
  private static State state;
  
  private static int titleFontLayer;
  private static boolean titleFontLayerIncr;
  
  // game
  public static int score;
  public static int level;
  private static int nextLevel;
  private static long levelStartTime;
  private static long levelEndTime;
  private static int direction;
  public static int largestSize;
  private static int drugDuration;
  public static long onDrugsSince;
  public static long time;
  
  public static boolean quit = false;

  
  public static void setUp() {
    // if fullscreen is requested set screen size to the actual resolution
    if(Settings.getBoolean("fullscreen")) {
      Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
      Settings.setSettingValue("display_width", d.width);
      Settings.setSettingValue("display_height", d.height);
    }
    Stats.load();
    mySquare = new Square(0, 0, Type.RED, Settings.getInt("sq_size"), 0);
    initRoundParameter();
    initFonts();
    initSound();
    //initStartMenuSquarez();
    //menuMusic.loop(-1);
  }
  
  public static void update() {
    time = System.currentTimeMillis();
    moveMouse();
    moveSquarez();
    addSquarez();
    checkIntersection();
    checkLevel();
  }

  public static void moveSquarez() {
    for (int i = 0; i < squarez.length; i++) {
      if ((squarez[i] != null)
              && (!squarez[i].move(level, (Logic.onDrugs() ? 1.5f : 1.0f) * 60/(float)Settings.getInt("framerate"),VIRTUAL_WIDTH, VIRTUAL_HEIGHT))) {
        squarez[i] = null;
      }
    }
    Log.debug("Logic.moveSquarez()");
  }

  public static void addSquarez() {
    if (Util.rand(1, 550 / ((direction % 2 == 1 ? VIRTUAL_WIDTH : VIRTUAL_HEIGHT) * Settings.getInt("sq_new_amount") / Settings.getInt("sq_size"))) == 1) {
      int newDirection = Settings.getBoolean("hardcore") || onDrugs() ? Util.rand(1, 4) : direction;
      int spawnX = 0;
      int spawnY = 0;
      switch (newDirection) {
        case 1:
          spawnX = Util.rand(0, VIRTUAL_WIDTH - Settings.getInt("sq_size"));
          spawnY = -Settings.getInt("sq_size");
          break;
        case 2:
          spawnX = VIRTUAL_WIDTH + Settings.getInt("sq_size");
          spawnY = Util.rand(0, VIRTUAL_HEIGHT - Settings.getInt("sq_size"));
          break;
        case 3:
          spawnX = Util.rand(0, VIRTUAL_WIDTH - Settings.getInt("sq_size"));
          spawnY = VIRTUAL_HEIGHT + Settings.getInt("sq_size");
          break;
        case 4:
          spawnX = -Settings.getInt("sq_size");
          spawnY = Util.rand(0, VIRTUAL_HEIGHT - Settings.getInt("sq_size"));
      }
      int rand = Util.rand(1, 1000);
      Type type = ((rand == 666) ? Type.MAGENTA : ((rand <= (100 / level) + 100) ? Type.GREEN : ((rand <= 300) ? Type.BLACK : ((rand <= 300 + 10 * level) ? Type.ORANGE : Type.BLUE))));
      addSquare(new Square(spawnX, spawnY, type, Settings.getInt("sq_size"), newDirection));
    }
    Log.debug("Logic.addSquarez()");
  }

  private static void addSquare(Square newSquare) {
    for (int i = 0; i < squarez.length; i++) {
      if (squarez[i] == null) {
        squarez[i] = newSquare;
        return;
      }
    }
  }
  
  public static void moveMouse() {
    mySquare.x = (Controller.mouseX - mySquare.size / 2);
    mySquare.y = (Controller.mouseY - mySquare.size / 2);
  }

  public static void checkIntersection() {
    for (int i = 0; i < squarez.length; i++) {
      if ((squarez[i] != null) && (mySquare.intersects(squarez[i]))) {
        switch (squarez[i].type) {
          case BLUE:
            score += Settings.getInt("sq_points");
            mySquare.grow(Settings.getInt("sq_grow"));
            break;
          case GREEN:
            if(mySquare.size > largestSize)
              largestSize = mySquare.size;
            mySquare.x += (mySquare.size - Settings.getInt("sq_size")) / 2;
            mySquare.y += (mySquare.size - Settings.getInt("sq_size")) / 2;
            mySquare.size = Settings.getInt("sq_size");
            break;
          case BLACK:
            if(mySquare.size > largestSize){
              largestSize = mySquare.size;
            }
            levelEndTime = (time - levelStartTime);
            Stats.save(new Stat(Settings.getString("username"), score, largestSize,
                    levelEndTime, time));
            state = State.END;
            //game.pause();
            initRoundParameter();
            //ingameMusic.stop();
            //menuMusic.loop(-1);
            return;
          case ORANGE:
            score += Settings.getInt("sq_points") * 10;
            changeDirection();
            break;
          case MAGENTA:
            score += Settings.getInt("sq_points") * 100;
            mySquare.grow(Settings.getInt("sq_grow") * 10);
            squarez = new Square[30];
            onDrugsSince = time;
        }
        squarez[i] = null;
      }
    }
    Log.debug("Logic.checkIntersection()");
  }

  private static void changeDirection() {
    for (int i = 0; i < squarez.length; i++) {
      if (squarez[i] != null) {
        // if hardcore or on drugs rotate clockwise or counterclockwiset else always rotate clockwise
        squarez[i].changeDirection(Settings.getBoolean("hardcore") || onDrugs() ? (Util.rand(1,2)*2)-1 : 1);
      }
    }
    if (!(Settings.getBoolean("hardcore") || onDrugs())) { // change spawn direction also
      direction = (direction % 4 + 1);
    }
  }

  public static void levelDown() {
    if (level != 1) {
      level -= 1;
      nextLevel = ((int) (nextLevel / Settings.getDouble("lvl_fac")));
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
    if(level != Settings.getInt("lvl_max")) {
      while (score > nextLevel) {
        level++; // level up
        nextLevel = ((int) (nextLevel * Settings.getDouble("lvl_fac")));
      }
    }
    Log.debug("Logic.checkLevel()");
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
  
  private static void initRoundParameter() {
    state = State.START;
    
    mySquare.size = Settings.getInt("sq_size");
    squarez = new Square[Settings.getInt("sq_new_amount") * 6];
    titleFontLayer = 1;
    titleFontLayerIncr = true;
    score = 0;
    level = Settings.getInt("lvl_start");
    nextLevel = Settings.getInt("lvl_next");
    largestSize = 0;
    drugDuration = 3;
    onDrugsSince = 0;
    direction = 1;
    time = System.currentTimeMillis();
    levelStartTime = time;
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

//  private static void initStartMenuSquarez() {
//    squarez = new Square[20];
//    for (int i = 0; i < squarez.length; i++) {
//        int rand = Util.rand(1, 1000);
//        Square newSquare = new Square(Util.rand(0, VIRTUAL_WIDTH - Settings.sq_size), Util.rand(0, VIRTUAL_HEIGHT - Settings.sq_size), rand == 666 ? 5 : rand <= 310 ? 4 : rand <= 300 ? 3 : rand <= 100 ? 2 : 1, Settings.sq_size, 0);
//
//        boolean intersects = false;
//        for (int j = 0; j < squarez.length; j++) {
//            if (squarez[j] != null) {
//                intersects |= newSquare.intersects(squarez[j]);
//            }
//        }
//        if (intersects) {
//            i--;
//        } else {
//            squarez[i] = newSquare;
//        }
//    }
//  }
  
  public static String getTime() {
    Date date = new Date(time - levelStartTime);
    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS", Locale.US);
    return sdf.format(date);
  }
  
  public static boolean onDrugs() {
    return onDrugsSince > time - drugDuration*1000;
  }
}