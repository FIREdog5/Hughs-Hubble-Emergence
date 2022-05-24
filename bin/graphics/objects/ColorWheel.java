package bin.graphics.objects;

import bin.ClientMain;
import bin.graphics.Color;

import com.jogamp.opengl.GL2;

public class ColorWheel {
  public static void draw(float radius, float x, float y) {


    GL2 gl = ClientMain.gl;
    int numVertices = (int)(radius * 2 * Math.PI * 5);

    double angle = 0;
    double angleIncrement = 2 * Math.PI / numVertices;

    Color innerColor = new Color("#ffffff");
    Color outerColor;

    gl.glBegin(GL2.GL_QUAD_STRIP);
    {
      for (int i = 0; i <= numVertices; i++) {
        Global.drawColor(innerColor);
        angle = i * angleIncrement;
        outerColor = Color.colorFromHSV(2f * (float)Math.PI - (float)angle, 1f, 1f);
        gl.glVertex2d(x, y);
        Global.drawColor(outerColor);
        double xO1 = radius * Math.cos(angle) + x;
        double yO1 = radius * Math.sin(angle) + y;
        gl.glVertex2d(xO1, yO1);
      }
    }
    gl.glEnd();
    gl.glFlush();


  }
}
