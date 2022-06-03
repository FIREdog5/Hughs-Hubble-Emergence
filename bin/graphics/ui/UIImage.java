package bin.graphics.ui;

import bin.graphics.objects.Image;
import bin.graphics.objects.Global;
import bin.graphics.Color;
import bin.resource.ImageResource;
import bin.resource.ImageResources;
import bin.Wrapper;

import java.lang.UnsupportedOperationException;

public class UIImage extends UIBox {

  public ImageResource image;
  public Wrapper<ImageResource> imageWrapper;
  public boolean useAA;

  public UIImage(UIElement parent) {
    super(parent);
    this.image = ImageResources.error;
    this.useAA = true;
  }

  public UIImage(UIElement parent, ImageResource image) {
    super(parent);
    this.image = image;
    this.useAA = true;
  }

  public UIImage(UIElement parent, Wrapper<ImageResource> imageWrapper) {
    super(parent);
    this.imageWrapper = imageWrapper;
    this.useAA = true;
  }

  @Override
  public float getWidth() {
    float minimum = Math.max(this.minWidth, this.margin * 2);
    if (this.maxWidth != -1) {
      minimum = Math.min(minimum, maxWidth);
    }
    return minimum;
  }

  @Override
  public float getHeight() {
    float minimum = Math.max(this.minHeight, this.margin * 2);
    if (this.maxHeight != -1) {
      minimum = Math.min(minimum, maxHeight);
    }
    return minimum;
  }

  @Override
  public float getChildX(int i) {
    throw new UnsupportedOperationException();
  }

  @Override
  public float getChildY(int i) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addChild(UIElement child) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void render() {
    if (this.image != null) {
      Global.drawColor(new Color("#ffffff"));
      if (this.useAA) {
        Image.draw(this.image, this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight());
      } else {
        Image.drawNoAA(this.image, this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight(), 0f);
      }
    }
    if (imageWrapper != null) {
      Global.drawColor(new Color("#ffffff"));
      if (this.useAA) {
        Image.draw(this.imageWrapper.get(), this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight());
      } else {
        Image.drawNoAA(this.imageWrapper.get(), this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight(), 0f);
      }
    }

  }
}
