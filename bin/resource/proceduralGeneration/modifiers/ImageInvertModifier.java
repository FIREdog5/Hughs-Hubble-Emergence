package bin.resource.proceduralGeneration.modifiers;

import bin.input.ClickHandler;
import bin.input.KeyboardHandler;
import bin.Wrapper;
import bin.graphics.ui.UIBoxCol;

import java.awt.image.BufferedImage;

import bin.resource.FastNoiseLite;
import bin.resource.proceduralGeneration.IImageModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.ImagePreprocessor;
import bin.resource.ImageResource;

public class ImageInvertModifier implements IImageModifier {
  public ImageInvertModifier() {
  }

  @Override
  public IImageModifier copy() {
    return new ImageInvertModifier();
  }

  @Override
  public BufferedImage resolve(BufferedImage image) {
    return ImagePreprocessor.invert(image);
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
