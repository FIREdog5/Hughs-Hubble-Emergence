package bin.resource.proceduralGeneration.modifiers;

import java.awt.image.BufferedImage;

import bin.resource.proceduralGeneration.IBasicImageModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.FastNoiseLite;;
import bin.resource.ImageGenerator;
import bin.resource.FastNoiseLite;


public class ImageCreateModifier implements IBasicImageModifier {
  private long seed;
  private double featureSize;
  private FastNoiseLite fnl;
  public ImageCreateModifier (long seed, double featureSize) {
    this.seed = seed;
    this.featureSize = featureSize;
  }
  public ImageCreateModifier (long seed, FastNoiseLite fnl) {
    this.seed = seed;
    this.fnl = fnl;
  }
  @Override
  public BufferedImage resolve(BufferedImage image) {
    if (fnl == null) {
      return ImageGenerator.SphericalOpenSimplexNoise(this.seed, this.featureSize, image.getWidth());
    } else {
      return ImageGenerator.SphericalFNL(this.fnl, this.seed, image.getWidth());
    }
  }
  @Override
  public Parameter[] getParameters() {
    Parameter[] parameters = {new Parameter("Feature Size", 1d, 100d)};
    return parameters;
  }
}
