package bin.graphics.objects;

import bin.ClientMain;
import bin.graphics.Color;

import com.jogamp.opengl.GL2;

public class GradientHalo{
  public static void draw(float outerRadius, float innerRadius, float x, float y, Color innerColor, Color outerColor) {
    GL2 gl = ClientMain.gl;
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    
    int numVertices = (int)(outerRadius * 2 * Math.PI * 5);

    double angle = 0;
    double angleIncrement = 2 * Math.PI / numVertices;
    gl.glBegin(GL2.GL_QUAD_STRIP);
    {
      for (int i = 0; i < numVertices; i++) {
        Global.drawColor(innerColor);
        angle = i * angleIncrement;
        double xI1 = innerRadius * Math.cos(angle) + x;
        double yI1 = innerRadius * Math.sin(angle) + y;
        gl.glVertex2d(xI1, yI1);
        Global.drawColor(outerColor);
        angle = i * angleIncrement;
        double xO1 = outerRadius * Math.cos(angle) + x;
        double yO1 = outerRadius * Math.sin(angle) + y;
        gl.glVertex2d(xO1, yO1);
        Global.drawColor(innerColor);
        angle = (i + 1) * angleIncrement;
        double xI2 = innerRadius * Math.cos(angle) + x;
        double yI2 = innerRadius * Math.sin(angle) + y;
        gl.glVertex2d(xI2, yI2);
        Global.drawColor(outerColor);
        angle = (i + 1) * angleIncrement;
        double xO2 = outerRadius * Math.cos(angle) + x;
        double yO2 = outerRadius * Math.sin(angle) + y;
        gl.glVertex2d(xO2, yO2);
      }
    }
    gl.glEnd();
    gl.glFlush();
  }
}
