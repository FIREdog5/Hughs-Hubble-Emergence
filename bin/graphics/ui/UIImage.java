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
  public float rotation;

  public UIImage(UIElement parent) {
    super(parent);
    this.image = ImageResources.error;
    this.useAA = true;
    this.rotation = 0;
  }

  public UIImage(UIElement parent, ImageResource image) {
    super(parent);
    this.image = image;
    this.useAA = true;
    this.rotation = 0;
  }

  public UIImage(UIElement parent, Wrapper<ImageResource> imageWrapper) {
    super(parent);
    this.imageWrapper = imageWrapper;
    this.useAA = true;
    this.rotation = 0;
  }

  public float getRotation() {
    return this.rotation;
  }

  private float getRotationRads(){
    return this.getRotation() / 180f * (float)Math.PI;
  }

  @Override
  public float getWidth() {
    float minimumWidth = Math.max(this.minWidth, this.margin * 2);
    if (this.maxWidth != -1) {
      minimumWidth = Math.min(minimumWidth, maxWidth);
    }
    float minimumHeight = Math.max(this.minHeight, this.margin * 2);
    if (this.maxHeight != -1) {
      minimumHeight = Math.min(minimumHeight, maxHeight);
    }
    float calcWidth = (float) (Math.abs(Math.sin(this.getRotationRads()) * minimumHeight) + Math.abs(Math.cos(this.getRotationRads()) * minimumWidth));
    return calcWidth;
  }

  @Override
  public float getHeight() {
    float minimumWidth = Math.max(this.minWidth, this.margin * 2);
    if (this.maxWidth != -1) {
      minimumWidth = Math.min(minimumWidth, maxWidth);
    }
    float minimumHeight = Math.max(this.minHeight, this.margin * 2);
    if (this.maxHeight != -1) {
      minimumHeight = Math.min(minimumHeight, maxHeight);
    }
    float calcHeight = (float) (Math.abs(Math.cos(this.getRotationRads()) * minimumHeight) + Math.abs(Math.sin(this.getRotationRads()) * minimumWidth));
    return calcHeight;
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
  public void addChild(UIElement child, int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void render() {
    float minimumWidth = Math.max(this.minWidth, this.margin * 2);
    if (this.maxWidth != -1) {
      minimumWidth = Math.min(minimumWidth, maxWidth);
    }
    float minimumHeight = Math.max(this.minHeight, this.margin * 2);
    if (this.maxHeight != -1) {
      minimumHeight = Math.min(minimumHeight, maxHeight);
    }
    if (this.image != null) {
      Global.drawColor(new Color("#ffffff"));
      if (this.useAA) {
        Image.draw(this.image, this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, minimumWidth, minimumHeight, this.getRotation());
      } else {
        Image.drawNoAA(this.image, this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, minimumWidth, minimumHeight, this.getRotation());
      }
    }
    if (imageWrapper != null) {
      Global.drawColor(new Color("#ffffff"));
      if (this.useAA) {
        Image.draw(this.imageWrapper.get(), this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, minimumWidth, minimumHeight, this.getRotation());
      } else {
        Image.drawNoAA(this.imageWrapper.get(), this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, minimumWidth, minimumHeight, this.getRotation());
      }
    }

  }
}
