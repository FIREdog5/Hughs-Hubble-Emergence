package bin.graphics.objects;

import bin.ClientMain;

import com.jogamp.opengl.GL2;

public class Pointer {
  public static void draw(float x, float y, float width, float height, String facing) {
    switch(facing) {
      case "right":
        draw(x, y, width, height, 0f);
        break;
      case "left":
        draw(x, y, width, height, 180f);
        break;
      case "up":
        draw(x, y, width, height, 90f);
        break;
      case "down":
        draw(x, y, width, height, 270f);
        break;
      default:
        draw(x, y, width, height, 0f);
        break;
    }
  }

  public static void draw(float x, float y, float width, float height, float rotation) {
    GL2 gl = ClientMain.gl;
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);

    gl.glPushMatrix();
    gl.glTranslatef(x, y, 0);
    gl.glRotatef(rotation, 0f, 0f, 1f);
    gl.glBegin(GL2.GL_POLYGON);
    {
      gl.glVertex2f(-width / 2, -height / 2);
      gl.glVertex2f(width / 2 - triangeHeight(height), -height / 2);
      gl.glVertex2f(width / 2, 0);
      gl.glVertex2f(width / 2 - triangeHeight(height), height / 2);
      gl.glVertex2f(-width / 2, height / 2);
    }
    gl.glEnd();
    gl.glFlush();
    gl.glRotatef(-rotation, 0f, 0f, 1f);
    gl.glTranslatef(-x, -y, 0);
    gl.glPopMatrix();
  }

  private static float triangeHeight(float a) {
    return (float)Math.sqrt(3) / 2 * a;
  }

}
