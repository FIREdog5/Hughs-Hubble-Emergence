package bin.resource.proceduralGeneration;

import bin.input.ClickHandler;
import bin.input.KeyboardHandler;
import bin.Wrapper;
import bin.graphics.ui.UIBoxCol;
import bin.resource.FastNoiseLite;
import bin.resource.ImageResource;

import java.awt.image.BufferedImage;

public abstract class ImageCombineModifier implements IImageModifier {
  protected FastNoiseLite fnl;

  public ImageCombineModifier(FastNoiseLite fnl) {
    this.fnl = fnl;
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
