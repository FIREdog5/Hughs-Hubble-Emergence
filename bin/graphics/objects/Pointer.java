package bin.graphics.objects;

import bin.ClientMain;

import com.jogamp.opengl.GL2;

public class Pointer {
  public static void draw(float x, float y, float width, float height, String facing) {
    GL2 gl = ClientMain.gl;
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    
    gl.glTranslatef(x, y, 0);
    gl.glBegin(GL2.GL_POLYGON);
    {
      switch(facing) {
        case "right":
          gl.glVertex2f(-width / 2, -height / 2);
          gl.glVertex2f(width / 2 - triangeHeight(height), -height / 2);
          gl.glVertex2f(width / 2, 0);
          gl.glVertex2f(width / 2 - triangeHeight(height), height / 2);
          gl.glVertex2f(-width / 2, height / 2);
          break;
        case "left":
          gl.glVertex2f(-width / 2 + triangeHeight(height), -height / 2);
          gl.glVertex2f(width / 2, -height / 2);
          gl.glVertex2f(width / 2, height / 2);
          gl.glVertex2f(-width / 2 + triangeHeight(height), height / 2);
          gl.glVertex2f(-width / 2, 0);
          break;
      }
    }
    gl.glEnd();
    gl.glFlush();
    gl.glTranslatef(-x, -y, 0);
  }

  private static float triangeHeight(float a) {
    return (float)Math.sqrt(3) / 2 * a;
  }

}
