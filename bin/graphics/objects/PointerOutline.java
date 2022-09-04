package bin.graphics.objects;

import bin.ClientMain;

import com.jogamp.opengl.GL2;

public class PointerOutline {
  public static void draw(float x, float y, float width, float height, String facing, float weight) {
    GL2 gl = ClientMain.gl;
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    
    gl.glPushMatrix();
    gl.glTranslatef(x, y, 0);
    gl.glBegin(GL2.GL_QUAD_STRIP);
    {
      switch(facing) {
        case "right":
          gl.glVertex2f(-width / 2, -height / 2);
          gl.glVertex2f(-width / 2 + weight, -height / 2 + weight);
          gl.glVertex2f(width / 2 - triangeHeight(height), -height / 2);
          gl.glVertex2f(width / 2 - triangeHeight(height), -height / 2 + weight);
          gl.glVertex2f(width / 2, 0);
          gl.glVertex2f(width / 2 - triangeHeight(2 * weight), 0);
          gl.glVertex2f(width / 2 - triangeHeight(height), height / 2);
          gl.glVertex2f(width / 2 - triangeHeight(height), height / 2 - weight);
          gl.glVertex2f(-width / 2, height / 2);
          gl.glVertex2f(-width / 2 + weight, height / 2 - weight);
          gl.glVertex2f(-width / 2, -height / 2);
          gl.glVertex2f(-width / 2 + weight, -height / 2 + weight);
          break;
        case "left":
          gl.glVertex2f(-width / 2 + triangeHeight(height), -height / 2);
          gl.glVertex2f(-width / 2 + triangeHeight(height), -height / 2 + weight);
          gl.glVertex2f(width / 2, -height / 2);
          gl.glVertex2f(width / 2 - weight, -height / 2 + weight);
          gl.glVertex2f(width / 2, height / 2);
          gl.glVertex2f(width / 2 - weight, height / 2 - weight);
          gl.glVertex2f(-width / 2 + triangeHeight(height), height / 2);
          gl.glVertex2f(-width / 2 + triangeHeight(height), height / 2 - weight);
          gl.glVertex2f(-width / 2, 0);
          gl.glVertex2f(-width / 2 + triangeHeight(2 * weight), 0);
          gl.glVertex2f(-width / 2 + triangeHeight(height), -height / 2);
          gl.glVertex2f(-width / 2 + triangeHeight(height), -height / 2 + weight);
          break;
      }
    }
    gl.glEnd();
    gl.glFlush();

    gl.glTranslatef(-x, -y, 0);
    gl.glPopMatrix();
  }

  private static float triangeHeight(float a) {
    return (float)Math.sqrt(3) / 2 * a;
  }

}
