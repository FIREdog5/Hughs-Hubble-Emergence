package bin.graphics.objects;

import bin.ClientMain;
import bin.resource.ImageResource;
import bin.resource.ImageResources;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

public class Image {
  public static void draw(ImageResource image, float x, float y, float width, float height, float rotation) {
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

    int frame = Global.getFrame();

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

  public static void draw(ImageResource image, float x, float y, float width, float height) {
    draw(image, x, y, width, height, 0);
  }
}
