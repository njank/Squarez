package game;

import java.awt.Font;
import java.io.InputStream;
import static game.Logic.VIRTUAL_HEIGHT;
import static game.Logic.VIRTUAL_WIDTH;
import org.newdawn.slick.TrueTypeFont;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.util.FontUtils;
import util.Util;

public class CustomFont {
  public static TrueTypeFont title;
  public static TrueTypeFont text;

  public static void setUp() {
      title = readFont("fipps.otf", VIRTUAL_WIDTH / 20f);
      text = readFont("monaco.ttf", VIRTUAL_HEIGHT / 20f);
  }
  
  private static TrueTypeFont readFont(String filename, float fontSize){
    try {
      InputStream inputStream = Util.classLoader.getResourceAsStream("res/fonts/"+filename);
      Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
      return new TrueTypeFont(awtFont.deriveFont(fontSize), false);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
  }
  
  public static void drawString(int posX, int posY, int width, String text, TrueTypeFont font, Color color, int alignment){
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    FontUtils.drawString(font, text, alignment, posX, posY, width, color);
    glDisable(GL_BLEND);
  }
  
  public static void drawLeft(int posX, int posY, String text, TrueTypeFont font, Color color, Color shadow){
    if(shadow != null)
      drawString(posX + font.getHeight()/10, posY + font.getHeight()/10, 0, text, font, shadow, FontUtils.Alignment.LEFT);
    drawString(posX, posY, 0, text, font, color, FontUtils.Alignment.LEFT);
  }
  
  public static void drawRight(int posX, int posY, String text, TrueTypeFont font, Color color){
    drawString(0, posY, posX, text, font, color, FontUtils.Alignment.RIGHT);
  }
  
  public static void drawCenteredString(int posY, String text, TrueTypeFont font, Color color){
    drawString(0, posY, VIRTUAL_WIDTH, text, font, color, FontUtils.Alignment.CENTER);
  }
  
  public static void drawCenteredString(int posX, int posY, int width, String text, TrueTypeFont font, Color color){
    drawString(posX, posY, width, text, font, color, FontUtils.Alignment.CENTER);
  }
}
