package bin.resource;

import bin.graphics.Renderer;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.IOException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import javax.imageio.ImageIO;

public class NoiseMaskedResource extends PolarResource{

  public NoiseMaskedResource(String path, float threshold, int frames, int frameDelay, boolean loops) {
    super(path, frames, frameDelay, loops);

    this.image = ImagePreprocessor.mask(this.image, threshold);

    //fix memory leak
    if (this.image != null) {
      this.image.flush();
    }
  }

  public NoiseMaskedResource(BufferedImage image, float threshold, int frames, int frameDelay, boolean loops) {
    super(image, frames, frameDelay, loops);

    this.image = ImagePreprocessor.mask(this.image, threshold);

    //fix memory leak
    if (this.image != null) {
      this.image.flush();
    }
  }

  public NoiseMaskedResource(String path, float threshold, int frames, int frameDelay) {
    this(path, threshold, frames, frameDelay, false);
  }

  public NoiseMaskedResource(BufferedImage image, float threshold, int frames, int frameDelay) {
    this(image, threshold, frames, frameDelay, false);
  }

  public NoiseMaskedResource(String path, float threshold) {
    this(path, threshold, 1, 1, false);
  }

  public NoiseMaskedResource(BufferedImage image, float threshold) {
    this(image, threshold, 1, 1, false);
  }

}
