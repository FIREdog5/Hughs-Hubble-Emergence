package bin.resource.proceduralGeneration.modifiers;

import java.awt.image.BufferedImage;

import bin.resource.FastNoiseLite;
import bin.resource.FastNoiseLiteDomainWarp;
import bin.resource.proceduralGeneration.IImageModifier;
import bin.resource.proceduralGeneration.ImageCombineModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.ImageGenerator;
import bin.resource.ImagePreprocessor;

public class ImageSubtractModifier extends ImageCombineModifier {
  public ImageSubtractModifier(FastNoiseLite fnl) {
    super(fnl);
  }

  @Override
  public IImageModifier copy() {
    return new ImageSubtractModifier(new FastNoiseLiteDomainWarp(this.fnl));
  }

  @Override
  public BufferedImage resolve(BufferedImage image1) {
    BufferedImage image2 = ImageGenerator.SphericalFNL(this.fnl, image1.getWidth());
    return ImagePreprocessor.combineImagesSubtract(image1, image2);
  }
}
