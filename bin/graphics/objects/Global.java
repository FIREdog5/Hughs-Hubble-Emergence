package bin.graphics.objects;

import bin.ClientMain;
import bin.graphics.Color;

import com.jogamp.opengl.GL2;
import java.util.Date;

public class Global{

  private static float frame = 0;
  private static long lastTime = 0L;

  public static void nextFrame() {
    Date date = new Date();
    long time = date.getTime();
    if (lastTime == 0) {
      lastTime = time;
      return;
    }
    frame += (time - lastTime) / 16.67f;
    lastTime = time;
    frame %= 1000000;
  }

  public static float getFrame() {
    return frame;
  }

  public static void drawColor(Color color) {
    GL2 gl = ClientMain.gl;
    gl.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
  }
}
