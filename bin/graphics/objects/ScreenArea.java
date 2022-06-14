package bin.graphics.objects;

import bin.ClientMain;
import bin.graphics.Renderer;

import com.jogamp.opengl.GL2;

public class ScreenArea {


  public static void draw(float x, float y, float width, float height) {
    GL2 gl = ClientMain.gl;

    float unitsWide = Renderer.unitsWide;
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / unitsWide);

    float lowU = (x + unitsWide / 2 - width / 2) / unitsWide;
    float highU = (x + unitsWide / 2 + width / 2) / unitsWide;
    float lowV = (y + unitsTall / 2 - height / 2) / unitsTall;
    float highV = (y + unitsTall / 2 + height / 2) / unitsTall;

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
  }
}
