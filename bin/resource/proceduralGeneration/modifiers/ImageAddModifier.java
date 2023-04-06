package bin.resource.proceduralGeneration.modifiers;

import java.awt.image.BufferedImage;
import java.lang.UnsupportedOperationException;

import bin.resource.FastNoiseLite;
import bin.resource.proceduralGeneration.IImageModifier;
import bin.resource.proceduralGeneration.ImageCombineModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.ImageGenerator;
import bin.resource.ImagePreprocessor;

public class ImageAddModifier extends ImageCombineModifier {
  public ImageAddModifier(FastNoiseLite fnl) {
    super(fnl);
  }

  @Override
  public IImageModifier copy() {
    return new ImageAddModifier(new FastNoiseLite(this.fnl));
  }

  @Override
  public BufferedImage resolve(BufferedImage image1) {
    BufferedImage image2 = ImageGenerator.SphericalFNL(this.fnl, image1.getWidth());
    return ImagePreprocessor.combineImagesAdd(image1, image2);
  }
}
