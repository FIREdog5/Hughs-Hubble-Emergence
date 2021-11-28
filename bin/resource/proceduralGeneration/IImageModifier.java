package bin.resource.proceduralGeneration;

import java.awt.image.BufferedImage;

public interface IImageModifier {
  public BufferedImage resolve(BufferedImage image);
  public Parameter[] getParameters();
}
