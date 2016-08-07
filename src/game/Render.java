package game;

import util.Log;
import static util.Settings.display_height;
import static util.Settings.display_width;
import util.Square;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import static game.Logic.VIRTUAL_WIDTH;
import static game.Logic.VIRTUAL_HEIGHT;

public class Render {

  public static int width;
  public static int height;
  public static int viewportX;
  public static int viewportY;
  public static float scaleX;
  public static float scaleY;

  public static void setUp() {
    float targetAspectRatio = VIRTUAL_WIDTH / (float)VIRTUAL_HEIGHT;

    // figure out the largest area that fits in this resolution at the desired aspect ratio
    width = display_width;
    height = (int) (width / targetAspectRatio + 0.5f);

    if (height > display_height) {
      //It doesn't fit our height, we must switch to pillarbox then
      height = display_height;
      width = (int) (height * targetAspectRatio + 0.5f);
    }

    // set up the new viewport centered in the backbuffer
    viewportX = (display_width / 2) - (width / 2);
    viewportY = (display_height / 2) - (height / 2);
    
    //Now to calculate the scale considering the screen size and virtual size
    scaleX = (float) ((float) (display_width) / (float) VIRTUAL_WIDTH);
    scaleY = (float) ((float) (display_height) / (float) VIRTUAL_HEIGHT);

    Log.logInfo("Render.setUp(" + width + ", " + height + ")");
  }

  public static void run() {
    beforeRun();

    drawSquarez();
    drawSquare(Logic.mySquare);
    drawHud();

    afterRun();
  }

  public static void beforeRun() {
    glViewport(viewportX, viewportY, width, height);

    // Now we use glOrtho
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();
    glLoadIdentity();
    // This function is for Mac and Windows only, if you are using
    // iOS you should use glOrthof instead
    glOrtho(0, display_width, display_height, 0, -1, 1);
    /*if on iOS*/ //glOrthof(0, screen_width, screen_height, 0, -1, 1);
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();

    // Push in scale transformations
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();

    //Now to calculate the scale considering the screen size and virtual size
    glScalef(scaleX, scaleY, 1.0f);

    // clear screen
    glColor3f(0.8f, 0.8f, 0.8f);
    drawQuad(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
  }

  public static void afterRun() {
    glMatrixMode(GL_PROJECTION); // edit the projection matrix
    glLoadIdentity(); // reset the projection matrix
    glOrtho(0, display_width, display_height, 0, 1, -1);
    glMatrixMode(GL_MODELVIEW); // switch back to the model view matrix
  }

  private static void drawSquare(Square s) {
    switch (s.type) {
      case 1: glColor3f(0.0f, 0.0f, 1.0f); break; // blue
      case 2: glColor3f(0.0f, 1.0f, 0.0f); break; // green
      case 3: glColor3f(0.0f, 0.0f, 0.0f); break; // black
      case 4: glColor3f(1.0f, 0.5f, 0.0f); break; // orange
      case 5: glColor3f(1.0f, 0.0f, 1.0f); break; // magenta
      case 0: glColor3f(1.0f, 0.0f, 0.0f); break; // red
    }
    drawQuad(s.x, s.y, s.size, s.size);
  }

  private static void drawCenteredQuad(int x, int y, int width, int height) {
    glBegin(GL_QUADS);
    glVertex2f(x - width / 2, y - height / 2);
    glVertex2f(x - width / 2 + width, y - height / 2);
    glVertex2f(x - width / 2 + width, y - height / 2 + height);
    glVertex2f(x - width / 2, y - height / 2 + height);
    glEnd();
  }

  private static void drawQuad(int x, int y, int width, int height) {
    glBegin(GL_QUADS);
    glVertex2f(x, y);
    glVertex2f(x + width, y);
    glVertex2f(x + width, y + height);
    glVertex2f(x, y + height);
    glEnd();
  }

  private static void drawHud() {
    CustomFont.drawLeft(10, 10, "points=" + Logic.score, CustomFont.text, Color.red, Color.yellow);
    CustomFont.drawLeft(10, 35, "level=" + Logic.level, CustomFont.text, Color.red, Color.yellow);
    CustomFont.drawLeft(10, 60, "size=" + Logic.mySquare.size, CustomFont.text, Color.red, Color.yellow);
    CustomFont.drawLeft(10, 85, "largestSize=" + Logic.largestSize, CustomFont.text, Color.red, Color.yellow);
    CustomFont.drawLeft(10, 110, "time=" + Logic.getTime(), CustomFont.text, Color.red, Color.yellow);
  }

  private static void drawSquarez() {
    for (int i = 0; i < Logic.squarez.length; i++) {
      if (Logic.squarez[i] != null) {
        drawSquare(Logic.squarez[i]);
      }
    }
  }
}
