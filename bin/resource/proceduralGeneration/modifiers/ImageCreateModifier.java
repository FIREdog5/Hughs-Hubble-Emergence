package bin.resource.proceduralGeneration.modifiers;

import bin.input.ClickHandler;
import bin.input.KeyboardHandler;
import bin.Wrapper;
import bin.graphics.ui.UIBoxCol;

import java.awt.image.BufferedImage;

import bin.resource.FastNoiseLite;
import bin.resource.proceduralGeneration.IImageModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.FastNoiseLite;;
import bin.resource.ImageGenerator;
import bin.resource.FastNoiseLite;
import bin.resource.ImageResource;

public class ImageCreateModifier implements IImageModifier {
  private double featureSize;
  private FastNoiseLite fnl;
  public ImageCreateModifier(FastNoiseLite fnl) {
    this.fnl = fnl;
  }

  @Override
  public IImageModifier copy() {
    return new ImageCreateModifier(new FastNoiseLite(this.fnl));
  }


  @Override
  public BufferedImage resolve(BufferedImage image) {
    if (fnl == null) {
      return null;
    } else {
      return ImageGenerator.SphericalFNL(this.fnl, image.getWidth());
    }
  }

  @Override
  public boolean hasParameter() {
    return false;
  }

  @Override
  public Runnable constructParameterFields(UIBoxCol container, ClickHandler clickHandler, KeyboardHandler keyboardHandler, BufferedImage imageIn, Wrapper<ImageResource> imageWrapper) {
    return null;
  }
}
