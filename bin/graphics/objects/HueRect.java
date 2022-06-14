package bin.graphics.objects;

import bin.ClientMain;
import bin.graphics.Color;

import com.jogamp.opengl.GL2;

public class HueRect {
  public static void draw(float x, float y, float width, float height, float rotation) {
    GL2 gl = ClientMain.gl;
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    gl.glTranslatef(x, y, 0);
    gl.glRotatef(rotation, 0, 0, 1);

    int numVertices = (int)(height * 10);
    Color color = new Color("#ffffff");
    float[] hsv = new float[] {2 * (float)Math.PI, 1, 1};

    gl.glBegin(GL2.GL_QUAD_STRIP);
    {
      for (int i = 0; i <= numVertices; i++) {
        hsv[0] = 2 * (float)Math.PI - ((float)i / (float)numVertices) * 2 * (float)Math.PI;
        color.setHSV(hsv);
        Global.drawColor(color);
        gl.glVertex2f(-width / 2, -height / 2 + height * ((float)i / (float)numVertices));
        gl.glVertex2f(width / 2, -height / 2 + height * ((float)i / (float)numVertices));
      }
    }
    gl.glEnd();
    gl.glFlush();

    gl.glRotatef(-rotation, 0, 0, 1);
    gl.glTranslatef(-x, -y, 0);
  }
}
