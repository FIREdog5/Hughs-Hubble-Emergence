package bin.graphics.objects;

import bin.ClientMain;

import com.jogamp.opengl.GL2;

public class Circle {
  public static void draw(float radius, float x, float y) {
    GL2 gl = ClientMain.gl;
    int numVertices = (int)(radius * 2 * Math.PI * 100);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    gl.glBegin(GL2.GL_POLYGON);
    {
        double angle = 0;
        double angleIncrement = 2 * Math.PI / numVertices;
        for (int i = 0; i < numVertices; i++) {
            angle = i * angleIncrement;
            double xC = radius * Math.cos(angle) + x;
            double yC = radius * Math.sin(angle) + y;
            gl.glVertex2d(xC, yC);
        }
    }
    gl.glEnd();
    gl.glFlush();
  }
}
