package bin.resource.proceduralGeneration.modifiers;

import java.awt.image.BufferedImage;

import bin.resource.FastNoiseLite;
import bin.resource.proceduralGeneration.IImageModifier;
import bin.resource.proceduralGeneration.ImageCombineModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.ImageGenerator;
import bin.resource.ImagePreprocessor;

public class ImageMultModifier extends ImageCombineModifier {
  public ImageMultModifier(FastNoiseLite fnl) {
    super(fnl);
  }

  @Override
  public IImageModifier copy() {
    return new ImageMultModifier(new FastNoiseLite(this.fnl));
  }

  @Override
  public BufferedImage resolve(BufferedImage image1) {
    BufferedImage image2 = ImageGenerator.SphericalFNL(this.fnl, image1.getWidth());
    return ImagePreprocessor.combineImagesMult(image1, image2);
  }
}
