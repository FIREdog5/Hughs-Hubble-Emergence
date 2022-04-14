package bin.graphics.ui;

import bin.graphics.Color;
import bin.graphics.objects.Global;
import bin.graphics.objects.RoundedBox;
import bin.graphics.objects.RoundedBoxOutline;

import java.lang.IndexOutOfBoundsException;

public abstract class UIBox extends UIElement implements IColorable, IOutlineable {
  public float radius;
  public Color color;
  public Color outlineColor;
  public float outlineWeight;
  public Boolean noBackground;

  public UIBox(UIElement parent) {
    super();
    this.parent = parent;
    this.minWidth = 0;
    this.minHeight = 0;
    this.margin = 0;
    this.padding = 0;
    this.radius = 0;
    this.noBackground = false;
    this.color = null;
    this.outlineColor = null;
    this.outlineWeight = 0;
  }

  @Override
  public Color getColor() {
    if (this.color != null) {
      return this.color;
    } else if (this.parent instanceof IColorable) {
      return ((IColorable)this.parent).getColor();
    } else {
      return new Color("#ffffff");
    }
  }

  @Override
  public Color getOutlineColor() {
    if (this.outlineColor != null) {
      return this.outlineColor;
    } else if (this.parent instanceof IOutlineable) {
      return ((IOutlineable)this.parent).getOutlineColor();
    } else {
      return new Color("#ffffff");
    }
  }

  @Override
  public float getX() {
    return this.parent.getChildX(parent.children.indexOf(this));
  }

  @Override
  public float getY() {
    return this.parent.getChildY(parent.children.indexOf(this));
  }

  @Override
  public float getPadding() {
    return this.padding;
  }

  @Override
  public float getMargin() {
    return this.margin;
  }

  @Override
  public void render() {
    if (!this.noBackground) {
      Global.drawColor(this.getColor());
      RoundedBox.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight(), this.radius);
    }
    if (this.outlineWeight > 0f) {
      Global.drawColor(this.getOutlineColor());
      RoundedBoxOutline.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight(), this.radius, this.outlineWeight);
    }
    for (int i = 0; i < this.children.size(); i++) {
      this.children.get(i).render();
    }
  }

  @Override
  public void cleanUp() {
    for (int i = 0; i < this.children.size(); i++) {
      UIElement child = this.children.get(i);
      child.parent = null;
      child.cleanUp();
    }
    this.children = null;
  }
}
