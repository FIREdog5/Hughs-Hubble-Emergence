package bin.graphics.objects;

import bin.ClientMain;

import com.jogamp.opengl.GL2;

public class Rect {
  public static void draw(float x, float y, float width, float height, float rotation) {
    GL2 gl = ClientMain.gl;
    gl.glTranslatef(x, y, 0);
    gl.glRotatef(rotation, 0, 0, 1);

    gl.glBegin(GL2.GL_QUADS);
    {
      gl.glVertex2f(-width / 2, -height / 2);
      gl.glVertex2f(width / 2, -height / 2);
      gl.glVertex2f(width / 2, height / 2);
      gl.glVertex2f(-width / 2, height / 2);
    }
    gl.glEnd();
    gl.glFlush();

    gl.glRotatef(-rotation, 0, 0, 1);
    gl.glTranslatef(-x, -y, 0);
  }

  public static void draw(float x, float y, float width, float height) {
    draw(x, y, width, height, 0);
  }

}
