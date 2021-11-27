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

    //
    // int colorFlipper = 0;
    //

    float increment = (float) Math.ceil(radius);
    float jInc = .1f;

    float rotFrame = (float)(rotTop / (2f * radius));

    gl.glBegin(GL2.GL_QUAD_STRIP);

    float xCoords[] = new float[4];
    float yCoords[] = new float[4];
    float uCoords[] = new float[4];
    float vCoords[] = new float[4];

    for(float c = -radius; c < radius; c+= jInc) {
      jInc = Math.max(((-1 * c * c / radius + radius) / 3), .1f);
      if (c > radius) {
        c = radius;
      }
      float j1 = c;
      float j2 = Math.min(c + jInc, radius);
      //
      // colorFlipper++;
      // colorFlipper %= 2;
      //
      yCoords[0] = j1 + y;
      yCoords[1] = j1 + y;
      yCoords[2] = j2 + y;
      yCoords[3] = j2 + y;
      float quadBase = frameBase + (float)(frameTop / (2 * radius)) * (float)(-j1 + radius);
      float quadTop = frameBase + (float)(frameTop / (2 * radius)) * (float)(-j2 + radius);

      float i1Inc = (float)Math.sqrt(radius * radius - j1 * j1) / radius;
      float i2Inc = (float)Math.sqrt(radius * radius - (j2) * (j2)) / radius;
      for(float i = -radius; i < radius - .001f; i+=radius / increment) {
        //
        // if(colorFlipper == 1){
        //   Graphics.drawColor(new Color("#ff0000"));
        // } else {
        //   Graphics.drawColor(new Color("#00ff00"));
        // }
        // Graphics.drawColor(new Color("#ffffff"));
        // colorFlipper++;
        // colorFlipper %= 2;
        //
        float i1 = i * i1Inc;
        float i2 = (i + radius / increment) * i1Inc;
        float i3 = (i + radius / increment) * i2Inc;
        float i4 = i * i2Inc;
        xCoords[0] = i1 + x;
        xCoords[1] = i2 + x;
        xCoords[2] = i3 + x;
        xCoords[3] = i4 + x;

        float quadLeft = rotBase + rotFrame * (float)(i + radius);
        float quadRight = rotBase + rotFrame * (float)(i + radius / increment + radius);
        uCoords[0] = quadLeft;
        uCoords[1] = quadRight;
        uCoords[2] = quadRight;
        uCoords[3] = quadLeft;
        vCoords[0] = quadBase;
        vCoords[1] = quadBase;
        vCoords[2] = quadTop;
        vCoords[3] = quadTop;
        gl.glTexCoord2f(uCoords[3], vCoords[3]);
        gl.glVertex2f(xCoords[3], yCoords[3]);
        gl.glTexCoord2f(uCoords[0], vCoords[0]);
        gl.glVertex2f(xCoords[0], yCoords[0]);
        gl.glTexCoord2f(uCoords[2], vCoords[2]);
        gl.glVertex2f(xCoords[2], yCoords[2]);
        gl.glTexCoord2f(uCoords[1], vCoords[1]);
        gl.glVertex2f(xCoords[1], yCoords[1]);
      }
      gl.glVertex2f(xCoords[2], yCoords[2]);
      gl.glTexCoord2f(uCoords[2], vCoords[2]);
      gl.glVertex2f(xCoords[2], yCoords[2]);
      gl.glTexCoord2f(uCoords[2], vCoords[2]);

      float i4 = -radius * i2Inc;
      xCoords[3] = i4 + x;

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
