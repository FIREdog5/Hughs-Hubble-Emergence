package bin.graphics.objects;

import bin.ClientMain;
import bin.graphics.Renderer;

import java.lang.IllegalStateException;
import com.jogamp.opengl.GL2;
import java.nio.FloatBuffer;

public class ReRendered {

  private static int[] textures = new int[1];
  private static boolean initialized = false;

  public static void initReRenderer() {
    //needs to be called each itteration of the render loop.
    //captures the screen as is.
    //do not call bind texture after this initialization.
    if(initialized) {
      throw new IllegalStateException("ReRenderer is already initialized");
    }
    GL2 gl = ClientMain.gl;
    gl.glGenTextures(1, textures, 0);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[0]);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    FloatBuffer buffer = FloatBuffer.allocate(4 * Renderer.getWindowWidth() * Renderer.getWindowHeight());
    gl.glReadBuffer(GL2.GL_BACK);
    gl.glReadPixels(0, 0, Renderer.getWindowWidth(), Renderer.getWindowHeight(), GL2.GL_RGBA, GL2.GL_FLOAT, buffer);
    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, Renderer.getWindowWidth(), Renderer.getWindowHeight(), 0, GL2.GL_RGBA, GL2.GL_FLOAT, buffer);
    initialized = true;
  }

  public static void cleanUpReRenderer() {
    //needs to be called after each initReRenderer call
    if(!initialized) {
      throw new IllegalStateException("ReRenderer is not initialized");
    }
    GL2 gl = ClientMain.gl;
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    gl.glDeleteTextures(1, textures, 0);
    initialized = false;
  }

  public static void reInitRenderer() {
    cleanUpReRenderer();
    initReRenderer();
  }

  public static void drawSquare(float x, float y, float width, float height) {
    //needs to be called after initReRenderer
    if(!initialized) {
      throw new IllegalStateException("ReRenderer is not initialized");
    }
    GL2 gl = ClientMain.gl;

    float unitsWide = Renderer.unitsWide;
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / unitsWide);

    float lowU = (x + unitsWide / 2 - width / 2) / unitsWide;
    float highU = (x + unitsWide / 2 + width / 2) / unitsWide;
    float lowV = (y + unitsTall / 2 - height / 2) / unitsTall;
    float highV = (y + unitsTall / 2 + height / 2) / unitsTall;

    gl.glPushMatrix();
    gl.glTranslatef(x, y, 0);

    gl.glBegin(GL2.GL_QUADS);
    {
      gl.glTexCoord2f(lowU, lowV);
      gl.glVertex2f(-width / 2, -height / 2);
      gl.glTexCoord2f(highU, lowV);
      gl.glVertex2f(width / 2, -height / 2);
      gl.glTexCoord2f(highU, highV);
      gl.glVertex2f(width / 2, height / 2);
      gl.glTexCoord2f(lowU, highV);
      gl.glVertex2f(-width / 2, height / 2);
    }
    gl.glEnd();
    gl.glFlush();

    gl.glTranslatef(-x, -y, 0);
    gl.glPopMatrix();
  }



}
