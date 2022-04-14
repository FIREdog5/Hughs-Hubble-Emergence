package bin.graphics.objects;

import bin.ClientMain;
import bin.graphics.Color;

import com.jogamp.opengl.GL2;

public class Global{

  private static int frame = 0;

  public static void nextFrame() {
    frame += 1;
    frame %= 1000000;
  }

  public static int getFrame() {
    return frame;
  }

  public static void drawColor(Color color) {
    GL2 gl = ClientMain.gl;
    gl.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
  }
}
