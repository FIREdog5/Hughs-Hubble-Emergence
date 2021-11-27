package bin.resource;

import bin.graphics.Renderer;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.IOException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import javax.imageio.ImageIO;

public class PolarResource extends ImageResource{

  public PolarResource(String path, int frames, int frameDelay, boolean loops) {
    super(path, frames, frameDelay, loops);

    this.image = ImagePreprocessor.wrapPoles(this.image);

    //fix memory leak
    if (this.image != null) {
      this.image.flush();
    }
  }

  public PolarResource(BufferedImage image, int frames, int frameDelay, boolean loops) {
    super(image, frames, frameDelay, loops);

    this.image = ImagePreprocessor.wrapPoles(this.image);

    //fix memory leak
    if (this.image != null) {
      this.image.flush();
    }
  }

  public PolarResource(String path, int frames, int frameDelay) {
    this(path, frames, frameDelay, false);
  }

  public PolarResource(BufferedImage image, int frames, int frameDelay) {
    this(image, frames, frameDelay, false);
  }

  public PolarResource(String path) {
    this(path, 1, 1, false);
  }

  public PolarResource(BufferedImage image) {
    this(image, 1, 1, false);
  }

}
