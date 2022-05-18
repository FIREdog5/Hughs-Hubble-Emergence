package bin.graphics.ui;

import bin.graphics.objects.Image;
import bin.resource.ImageResource;
import bin.resource.ImageResources;

import java.lang.UnsupportedOperationException;

public class UIImage extends UIBox {

  public ImageResource image;

  public UIImage(UIElement parent) {
    super(parent);
    this.image = ImageResources.error;
  }

  public UIImage(UIElement parent, ImageResource image) {
    super(parent);
    this.image = image;
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
    if (this.image == null) {
      return;
    }
    Image.draw(this.image, this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight());
  }
}
