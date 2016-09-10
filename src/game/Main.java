package game;

import util.Settings;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.newdawn.slick.opengl.ImageIOImageData;
import util.Log;
import util.Util;

public class Main {

  public Main() {
    System.setProperty("java.library.path", "lib");
    System.setProperty("org.lwjgl.librarypath", new File("lib/native").getAbsolutePath());

    initializeProgram();
    programLoop();
    exitProgram(false);
  }

  private void initializeProgram() {
    Logic.setUp();
    initializeDisplay();
    Controller.setUp();
    CustomFont.setUp();
    Render.setUp();
  }

  private void initializeDisplay() {
    setDisplayMode(Settings.getInt("display_width"), Settings.getInt("display_height"), Settings.getBoolean("fullscreen"));
    Display.setVSyncEnabled(Settings.getBoolean("vsync"));
    Display.setTitle(Settings.DISPLAY_TITLE);
    
    try {
      Display.create();
    } catch (LWJGLException ex) {
      ex.printStackTrace();
      Display.destroy();
      System.exit(1);
    }
    setIcons();
  }

  private static void setDisplayMode(int width, int height, boolean fullscreen) {    
    // return if requested DisplayMode is already set
    if ((Display.getDisplayMode().getWidth() == width)
            && (Display.getDisplayMode().getHeight() == height)
            && (Display.isFullscreen() == fullscreen)) {
      return;
    }

    try {
      DisplayMode targetDisplayMode = null;

      if (fullscreen) {
        DisplayMode[] modes = Display.getAvailableDisplayModes();
        int freq = 0;

        for (int i = 0; i < modes.length; i++) {
          DisplayMode current = modes[i];

          if ((current.getWidth() == width) && (current.getHeight() == height)) {
            if ((targetDisplayMode == null) || (current.getFrequency() >= freq
                    && (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel()))) {
              targetDisplayMode = current;
              freq = targetDisplayMode.getFrequency();
            }

                        // if we've found a match for bpp and frequence against the
            // original display mode then it's probably best to go for this one
            // since it's most likely compatible with the monitor
            if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel())
                    && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
              targetDisplayMode = current;
              break;
            }
          }
        }
      } else {
        targetDisplayMode = new DisplayMode(width, height);
      }

      if (targetDisplayMode == null) {
        Log.error("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
        return;
      }

      Display.setDisplayMode(targetDisplayMode);
      Display.setFullscreen(fullscreen);

    } catch (LWJGLException e) {
      Log.error("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
    }
  }

  private void programLoop() {
    while (!Display.isCloseRequested() && !Logic.quit) {
      Logic.update();
      Controller.update();
      Render.run();

      Display.update();
      Display.sync(Settings.getInt("framerate"));
    }
  }

  private void exitProgram(boolean asCrash) {
    destroyBuffers();
    Display.destroy();
    System.exit(asCrash ? 1 : 0);
  }

  private void destroyBuffers() {
    // TODO: destroy buffers
  }
  
  public void setIcons() {
    try {
      Display.setIcon(new ByteBuffer[] {
        new ImageIOImageData().loadImage(Util.classLoader.getResourceAsStream("res/img/window/icon16.png")),
        new ImageIOImageData().loadImage(Util.classLoader.getResourceAsStream("res/img/window/icon32.png")),
        new ImageIOImageData().loadImage(Util.classLoader.getResourceAsStream("res/img/window/icon64.png")),
        new ImageIOImageData().loadImage(Util.classLoader.getResourceAsStream("res/img/window/icon128.png")),
        new ImageIOImageData().loadImage(Util.classLoader.getResourceAsStream("res/img/window/icon256.png"))
      });
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) {
    new Main();
  }
}
