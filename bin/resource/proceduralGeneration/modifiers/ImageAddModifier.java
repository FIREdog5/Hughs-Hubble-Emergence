package bin.resource.proceduralGeneration.modifiers;

import java.awt.image.BufferedImage;

import bin.resource.proceduralGeneration.ImageCombineModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.proceduralGeneration.SimpleImage;
import bin.resource.ImagePreprocessor;

public class ImageAddModifier extends ImageCombineModifier {
  public ImageAddModifier (SimpleImage simpleImage) {
    super(simpleImage);
  }
  @Override
  public BufferedImage resolve(BufferedImage image1) {
    BufferedImage image2 = this.simpleImage.resolve(image1.getWidth(), image1.getHeight());
    return ImagePreprocessor.combineImagesAdd(image1, image2);
  }
}
