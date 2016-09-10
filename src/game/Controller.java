package game;

import util.Settings;
import org.lwjgl.input.*;

public class Controller {
  public static int mouseX;
  public static int mouseY;
  
  public static void setUp() {
    //Mouse.setGrabbed(true); // hide mouse
  }

  public static void update() {
    setMouse();
    
    Logic.quit = Keyboard.isKeyDown(Keyboard.KEY_ESCAPE);
  }

  private static void setMouse() {
    mouseX = (int)(Mouse.getX() / Render.scaleX);
    mouseY = (int)((Settings.getInt("display_height") - Mouse.getY()) / Render.scaleY);
    
    if(mouseX < Logic.mySquare.size/2)
      mouseX = Logic.mySquare.size/2;
    if(mouseX > Logic.VIRTUAL_WIDTH - Logic.mySquare.size/2)
      mouseX = Logic.VIRTUAL_WIDTH - Logic.mySquare.size/2;
    if(mouseY < Logic.mySquare.size/2)
      mouseY = Logic.mySquare.size/2;
    if(mouseY > Logic.VIRTUAL_HEIGHT - Logic.mySquare.size/2)
      mouseY = Logic.VIRTUAL_HEIGHT - Logic.mySquare.size/2;
  }
}
