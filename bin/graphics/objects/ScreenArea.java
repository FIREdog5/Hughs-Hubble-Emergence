package bin.graphics.objects;

import bin.ClientMain;
import bin.graphics.Renderer;

import com.jogamp.opengl.GL2;

public class ScreenArea {

  public static void draw(float x, float y, float width, float height) {
    draw(x, y, x, y, width, height);
  }

  public static void draw(float x, float y, float newX, float newY, float width, float height) {
    GL2 gl = ClientMain.gl;

    float unitsWide = Renderer.unitsWide;
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / unitsWide);

    float lowU = (x + unitsWide / 2f - width / 2f) / unitsWide;
    float highU = (x + unitsWide / 2f + width / 2f) / unitsWide;
    float lowV = (y + unitsTall / 2f - height / 2f) / unitsTall;
    float highV = (y + unitsTall / 2f + height / 2f) / unitsTall;
    
    gl.glPushMatrix();
    gl.glTranslatef(newX, newY, 0);

    gl.glBegin(GL2.GL_QUADS);
    {
      gl.glTexCoord2f(lowU, lowV);
      gl.glVertex2f(-width / 2f, -height / 2f);
      gl.glTexCoord2f(highU, lowV);
      gl.glVertex2f(width / 2f, -height / 2f);
      gl.glTexCoord2f(highU, highV);
      gl.glVertex2f(width / 2f, height / 2f);
      gl.glTexCoord2f(lowU, highV);
      gl.glVertex2f(-width / 2f, height / 2f);
    }
    gl.glEnd();
    gl.glFlush();

    gl.glTranslatef(-newX, -newY, 0);
    gl.glPopMatrix();
  }
}
