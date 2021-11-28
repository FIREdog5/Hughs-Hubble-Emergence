package bin.resource.proceduralGeneration.modifiers;

import java.awt.image.BufferedImage;

import bin.resource.proceduralGeneration.IBasicImageModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.ImageGenerator;

public class ImageCreateModifier implements IBasicImageModifier {
  private long seed;
  private double featureSize;
  public ImageCreateModifier (long seed, double featureSize) {
    this.seed = seed;
    this.featureSize = featureSize;
  }
  @Override
  public BufferedImage resolve(BufferedImage image) {
    return ImageGenerator.OpenSimpleNoiseTexture(this.seed, this.featureSize, image.getWidth(), image.getHeight());
  }
  @Override
  public Parameter[] getParameters() {
    Parameter[] parameters = {new Parameter("Feature Size", 1d, 100d)};
    return parameters;
  }
}
