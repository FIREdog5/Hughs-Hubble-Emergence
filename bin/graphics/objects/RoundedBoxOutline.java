package bin.graphics.objects;

import bin.ClientMain;

import com.jogamp.opengl.GL2;

public class RoundedBoxOutline {
  public static void draw(float x, float y, float width, float height, float radius, float weight) {
    GL2 gl = ClientMain.gl;
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    
    gl.glTranslatef(x, y, 0);

    gl.glBegin(GL2.GL_QUAD_STRIP);

    //top edge
    gl.glVertex2f(-width / 2f + radius, height / 2f);
    gl.glVertex2f(-width / 2f + radius, height / 2f - weight);
    gl.glVertex2f(width / 2f - radius, height / 2f);
    gl.glVertex2f(width / 2f - radius, height / 2f - weight);

    //top right corner
    int divisions = (int)(radius * 2 * Math.PI * 5);
    if (divisions < 2) {
      divisions = 2;
    }
    float centerX = width / 2f - radius;
    float centerY = height / 2f - radius;
    for (int i = 1; i <= divisions; i++) {
      float theta = (float)(Math.PI / 2f) - (float)(Math.PI / 2f) * (float)(i / (float)divisions);
      float outerDX = radius * (float)Math.cos(theta);
      float outerDY = radius * (float)Math.sin(theta);
      float innerDX = (radius - weight) * (float)Math.cos(theta);
      float innerDY = (radius - weight) * (float)Math.sin(theta);
      gl.glVertex2f(centerX + outerDX, centerY + outerDY);
      gl.glVertex2f(centerX + innerDX, centerY + innerDY);
    }

    //right edge
    gl.glVertex2f(width / 2f, -height / 2f + radius);
    gl.glVertex2f(width / 2f - weight, -height / 2f + radius);

    //bottom right corner
    centerX = width / 2f - radius;
    centerY = -height / 2f + radius;
    for (int i = 1; i <= divisions; i++) {
      float theta = -1f * (float)(Math.PI / 2f) * (float)(i / (float)divisions);
      float outerDX = radius * (float)Math.cos(theta);
      float outerDY = radius * (float)Math.sin(theta);
      float innerDX = (radius - weight) * (float)Math.cos(theta);
      float innerDY = (radius - weight) * (float)Math.sin(theta);
      gl.glVertex2f(centerX + outerDX, centerY + outerDY);
      gl.glVertex2f(centerX + innerDX, centerY + innerDY);
    }

    //bottom edge
    gl.glVertex2f(-width / 2f + radius, -height / 2f);
    gl.glVertex2f(-width / 2f + radius, -height / 2f + weight);

    //bottom left corner
    centerX = -width / 2f + radius;
    centerY = -height / 2f + radius;
    for (int i = 1; i <= divisions; i++) {
      float theta = (float)(Math.PI * 3f / 2f) - (float)(Math.PI / 2f) * (float)(i / (float)divisions);
      float outerDX = radius * (float)Math.cos(theta);
      float outerDY = radius * (float)Math.sin(theta);
      float innerDX = (radius - weight) * (float)Math.cos(theta);
      float innerDY = (radius - weight) * (float)Math.sin(theta);
      gl.glVertex2f(centerX + outerDX, centerY + outerDY);
      gl.glVertex2f(centerX + innerDX, centerY + innerDY);
    }

    //left edge
    gl.glVertex2f(-width / 2f, height / 2f - radius);
    gl.glVertex2f(-width / 2f + weight, height / 2f - radius);

    //top left corner
    centerX = -width / 2f + radius;
    centerY = height / 2f - radius;
    for (int i = 1; i <= divisions; i++) {
      float theta = (float)Math.PI - (float)(Math.PI / 2f) * (float)(i / (float)divisions);
      float outerDX = radius * (float)Math.cos(theta);
      float outerDY = radius * (float)Math.sin(theta);
      float innerDX = (radius - weight) * (float)Math.cos(theta);
      float innerDY = (radius - weight) * (float)Math.sin(theta);
      gl.glVertex2f(centerX + outerDX, centerY + outerDY);
      gl.glVertex2f(centerX + innerDX, centerY + innerDY);
    }

    gl.glEnd();
    gl.glFlush();

    gl.glTranslatef(-x, -y, 0);
  }
}
