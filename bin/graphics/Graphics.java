package bin.graphics;

import bin.ClientMain;
import bin.resource.ImageResource;
import bin.resource.ImageResources;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

public class Graphics{

  private static int frame = 0;

  public static void nextFrame() {
    frame += 1;
    frame %= 1000000;
  }

  public static void drawColor(Color color) {
    GL2 gl = ClientMain.gl;
    gl.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
  }

  public static void drawCircle(float radius, float x, float y) {
    GL2 gl = ClientMain.gl;
    int numVertices = (int)(radius * 2 * Math.PI * 100);
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

  public static void drawGradientHalo(float outerRadius, float innerRadius, float x, float y, Color innerColor, Color outerColor) {
    GL2 gl = ClientMain.gl;
    int numVertices = (int)(outerRadius * 2 * Math.PI * 5);

    double angle = 0;
    double angleIncrement = 2 * Math.PI / numVertices;
    gl.glBegin(GL2.GL_QUAD_STRIP);
    {
      for (int i = 0; i < numVertices; i++) {
        drawColor(innerColor);
        angle = i * angleIncrement;
        double xI1 = innerRadius * Math.cos(angle) + x;
        double yI1 = innerRadius * Math.sin(angle) + y;
        gl.glVertex2d(xI1, yI1);
        drawColor(outerColor);
        angle = i * angleIncrement;
        double xO1 = outerRadius * Math.cos(angle) + x;
        double yO1 = outerRadius * Math.sin(angle) + y;
        gl.glVertex2d(xO1, yO1);
        drawColor(innerColor);
        angle = (i + 1) * angleIncrement;
        double xI2 = innerRadius * Math.cos(angle) + x;
        double yI2 = innerRadius * Math.sin(angle) + y;
        gl.glVertex2d(xI2, yI2);
        drawColor(outerColor);
        angle = (i + 1) * angleIncrement;
        double xO2 = outerRadius * Math.cos(angle) + x;
        double yO2 = outerRadius * Math.sin(angle) + y;
        gl.glVertex2d(xO2, yO2);
      }
    }
    gl.glEnd();
    gl.glFlush();
  }

  public static void drawRect(float x, float y, float width, float height, float rotation) {
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

  public static void drawRect(float x, float y, float width, float height) {
    drawRect(x, y, width, height, 0);
  }

  public static void drawImage(ImageResource image, float x, float y, float width, float height, float rotation) {
    GL2 gl = ClientMain.gl;

    Texture texture = null;

    if (image != null) {
    texture = image.getTexture();
    }

    if (texture == null) {
      texture = ImageResources.error.getTexture();
    }

    gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureObject());

    gl.glTranslatef(x, y, 0);
    gl.glRotatef(rotation, 0, 0, 1);

    int currentFrame = (int) (frame / image.getFrameDelay()) % image.getFrames();

    if (image.getLoops()) {
      if ((int)(frame / image.getFrameDelay()) % (image.getFrames() * 2) >= image.getFrames()) {
        currentFrame = image.getFrames() - currentFrame - 1;
      }
    }

    float frameBase = 1f / image.getFrames() * currentFrame;
    float frameTop = 1f / image.getFrames() * (currentFrame + 1);

    gl.glBegin(GL2.GL_QUADS);
    {
      gl.glTexCoord2f(0, frameTop);
      gl.glVertex2f(-width / 2, -height / 2);
      gl.glTexCoord2f(1, frameTop);
      gl.glVertex2f(width / 2, -height / 2);
      gl.glTexCoord2f(1, frameBase);
      gl.glVertex2f(width / 2, height / 2);
      gl.glTexCoord2f(0, frameBase);
      gl.glVertex2f(-width / 2, height / 2);
    }
    gl.glEnd();
    gl.glFlush();

    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);

    gl.glRotatef(-rotation, 0, 0, 1);
    gl.glTranslatef(-x, -y, 0);
  }

  public static void drawImage(ImageResource image, float x, float y, float width, float height) {
    drawImage(image, x, y, width, height, 0);
  }

  public static void drawGlobe(ImageResource image, float radius, float x, float y) {
    GL2 gl = ClientMain.gl;

    Texture texture = null;

    if (image != null) {
    texture = image.getTexture();
    }

    if (texture == null) {
      texture = ImageResources.error.getTexture();
    }

    gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureObject());

    int currentFrame = (int) (frame / image.getFrameDelay()) % image.getFrames();

    if (image.getLoops()) {
      if ((int)(frame / image.getFrameDelay()) % (image.getFrames() * 2) >= image.getFrames()) {
        currentFrame = image.getFrames() - currentFrame - 1;
      }
    }

    float frameBase = 1f / image.getFrames() * currentFrame;
    float frameTop = 1f / image.getFrames() * (currentFrame + 1);

    float textureDim = frameTop - frameBase;

    float rotBase = (frame % image.getFrameDelay()) / (float)image.getFrameDelay() * 0.5f;
    float rotTop = 0.25f;

    // color flipping for debugging
    // int colorFlipper = 0;
    // end color flipping for debugging

    // decide how many increments are needed for this zoom
    // 8 is magic number, enough for the little guys, not too much for the big guys... TODO make non-linear, its probably too much for the big guys
    float increment = (float) Math.ceil(radius) * 8;

    float rotFrame = (float)(rotTop / (2f * radius));

    gl.glBegin(GL2.GL_QUAD_STRIP);

    float xCoords[] = new float[4];
    float yCoords[] = new float[4];
    float uCoords[] = new float[4];
    float vCoords[] = new float[4];

    for(float c = 0f; c < increment; c+= 1) {
      float theta1 = c/increment * (float)Math.PI;
      float theta2 = (c+1f)/increment * (float)Math.PI;
      float j1 = (float)(radius * Math.cos(theta1));
      float j2 = (float)(radius * Math.cos(theta2));
      // color flipping for debugging
      // colorFlipper++;
      // colorFlipper %= 2;
      // end color flipping for debugging
      yCoords[0] = j1 + y;
      yCoords[1] = j1 + y;
      yCoords[2] = j2 + y;
      yCoords[3] = j2 + y;
      float quadBase = frameBase + (float)((frameTop - frameBase) / increment) * c;
      float quadTop = frameBase + (float)((frameTop - frameBase) / increment) * (c+1f);

      float i1Inc = (float)Math.sqrt(radius * radius - j1 * j1) / radius;
      float i2Inc = (float)Math.sqrt(radius * radius - (j2) * (j2)) / radius;

      float width1 = radius * (float)Math.sin(theta1);
      float width2 = radius * (float)Math.sin(theta2);

      for(float i = 0f; i < increment; i+=1) {
        // color flipping for debugging
        // if(colorFlipper == 1){
        //   Graphics.drawColor(new Color("#ff0000"));
        // } else {
        //   Graphics.drawColor(new Color("#00ff00"));
        // }
        // // Graphics.drawColor(new Color("#ffffff"));
        // colorFlipper++;
        // colorFlipper %= 2;
        // end color flipping for debugging

        float theta3 = i/increment * (float)Math.PI;
        float theta4 = (i+1f)/increment * (float)Math.PI;
        float i1 = -(float)(width1 * Math.cos(theta3));
        float i2 = -(float)(width2 * Math.cos(theta3));
        float i3 = -(float)(width1 * Math.cos(theta4));
        float i4 = -(float)(width2 * Math.cos(theta4));

        xCoords[0] = i1 + x;
        xCoords[1] = i3 + x;
        xCoords[2] = i4 + x;
        xCoords[3] = i2 + x;

        float quadLeft = rotBase + rotTop * (float)(i / increment);
        float quadRight = rotBase + rotTop * (float)((i+1) / increment);
        uCoords[0] = quadLeft;
        uCoords[1] = quadRight;
        uCoords[2] = quadRight;
        uCoords[3] = quadLeft;
        vCoords[0] = quadBase;
        vCoords[1] = quadBase;
        vCoords[2] = quadTop;
        vCoords[3] = quadTop;

        //I think the first half of these are useless?
        // gl.glTexCoord2f(uCoords[3], vCoords[3]);
        // gl.glVertex2f(xCoords[3], yCoords[3]);
        // gl.glTexCoord2f(uCoords[0], vCoords[0]);
        // gl.glVertex2f(xCoords[0], yCoords[0]);
        gl.glTexCoord2f(uCoords[2], vCoords[2]);
        gl.glVertex2f(xCoords[2], yCoords[2]);
        gl.glTexCoord2f(uCoords[1], vCoords[1]);
        gl.glVertex2f(xCoords[1], yCoords[1]);
      }
      gl.glVertex2f(xCoords[2], yCoords[2]);
      gl.glTexCoord2f(uCoords[2], vCoords[2]);
      gl.glVertex2f(xCoords[2], yCoords[2]);
      gl.glTexCoord2f(uCoords[2], vCoords[2]);

      xCoords[3] = x - width2;

      gl.glVertex2f(xCoords[3], yCoords[3]);
      gl.glTexCoord2f(uCoords[3], vCoords[3]);
      gl.glVertex2f(xCoords[3], yCoords[3]);
      gl.glTexCoord2f(uCoords[3], vCoords[3]);
    }
    gl.glEnd();
    gl.glFlush();
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
  }

}
