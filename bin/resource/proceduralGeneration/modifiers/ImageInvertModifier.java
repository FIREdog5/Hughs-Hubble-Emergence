package bin.resource.proceduralGeneration.modifiers;

import java.awt.image.BufferedImage;

import bin.resource.proceduralGeneration.IBasicImageModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.ImagePreprocessor;

public class ImageInvertModifier implements IBasicImageModifier {
  public ImageInvertModifier () {

  }
  @Override
  public BufferedImage resolve(BufferedImage image) {
    return ImagePreprocessor.invert(image);
  }
  @Override
  public Parameter[] getParameters() {
    return new Parameter[0];
  }
}
