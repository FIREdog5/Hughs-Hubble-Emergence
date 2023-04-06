package bin.resource.proceduralGeneration;

import bin.input.ClickHandler;
import bin.input.KeyboardHandler;
import bin.Wrapper;
import bin.graphics.ui.UIBoxCol;
import bin.resource.ImageResource;

import java.awt.image.BufferedImage;

public interface IImageModifier {
  public IImageModifier copy();
  public BufferedImage resolve(BufferedImage image);
  public boolean hasParameter();
  public Runnable constructParameterFields(UIBoxCol container, ClickHandler clickHandler, KeyboardHandler keyboardHandler, BufferedImage imageIn, Wrapper<ImageResource> imageWrapper);
}
