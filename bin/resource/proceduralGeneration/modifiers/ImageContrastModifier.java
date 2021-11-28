package bin.resource.proceduralGeneration.modifiers;

import java.awt.image.BufferedImage;

import bin.resource.proceduralGeneration.IBasicImageModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.ImagePreprocessor;

public class ImageContrastModifier implements IBasicImageModifier {
  private float scale;
  private float offset;
  public ImageContrastModifier (double scale, double offset) {
    this.scale = (float)scale;
    this.offset = (float)offset;
  }
  @Override
  public BufferedImage resolve(BufferedImage image) {
    return ImagePreprocessor.adjustContrast(image, this.scale, this.offset);
  }
  @Override
  public Parameter[] getParameters() {
    Parameter[] parameters = {new Parameter("Scale aka contrast", 0d, 10d), new Parameter("Offset aka brightness", -255d, 255d)};
    return parameters;
  }
}
