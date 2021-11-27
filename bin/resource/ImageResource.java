package bin.resource;

import bin.graphics.Renderer;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.IOException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import javax.imageio.ImageIO;

public class ImageResource {

  protected Texture texture = null;
  protected BufferedImage image = null;
  protected int frames;
  protected int frameDelay;
  protected boolean loops;

  public ImageResource(String path, int frames, int frameDelay, boolean loops) {
    URL url = ImageResource.class.getResource(path);
    try {
      image = ImageIO.read(url);
    } catch (IOException e) {
      e.printStackTrace();
    }

    //fix memory leak
    if (image != null) {
      image.flush();
    }

    this.frames = frames;
    this.frameDelay = frameDelay;
    this.loops = loops;
  }

  public ImageResource(BufferedImage image, int frames, int frameDelay, boolean loops) {
    this.image = image;
    this.frames = frames;
    this.frameDelay = frameDelay;
    this.loops = loops;
  }

  public ImageResource(String path, int frames, int frameDelay) {
    this(path, frames, frameDelay, false);
  }

  public ImageResource(BufferedImage image, int frames, int frameDelay) {
    this(image, frames, frameDelay, false);
  }

  public ImageResource(String path) {
    this(path, 1, 1, false);
  }

  public ImageResource(BufferedImage image) {
    this(image, 1, 1, false);
  }

  public ImageResource() {
    this("", 1, 1, false);
  }

  public Texture getTexture() {
    if (image == null) {
      return null;
    }
    if (this.texture == null) {
      this.texture = AWTTextureIO.newTexture(Renderer.getProfile(), image, true);
    }
    return this.texture;
  }

  public int getFrames() {
    return frames;
  }

  public int getFrameDelay() {
    return frameDelay;
  }

  public boolean getLoops() {
    return this.loops;
  }

}
