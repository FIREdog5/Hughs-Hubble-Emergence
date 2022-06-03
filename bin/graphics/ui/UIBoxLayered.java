package bin.graphics.ui;

import java.lang.IndexOutOfBoundsException;

public class UIBoxLayered extends UIBox {

  public UIBoxLayered(UIElement parent) {
    super(parent);
  }

  @Override
  public float getWidth() {
    float minimum = Math.max(this.minWidth, this.margin * 2);
    for (int i = 0; i < this.children.size(); i++) {
      UIElement child = this.children.get(i);
      float width = Math.max(child.x, this.margin + child.getPadding()) + child.getPadding() + this.margin + child.getWidth();
      minimum = Math.max(minimum, width);
    }
    if (this.maxWidth != -1) {
      minimum = Math.min(minimum, maxWidth);
    }
    return minimum;
  }

  @Override
  public float getHeight() {
    float minimum = Math.max(this.minHeight, this.margin * 2);
    for (int i = 0; i < this.children.size(); i++) {
      UIElement child = this.children.get(i);
      float height = Math.max(child.y, this.margin + child.getPadding()) + child.getPadding() + this.margin + child.getHeight();
      minimum = Math.max(minimum, height);
    }
    if (this.maxHeight != -1) {
      minimum = Math.min(minimum, maxHeight);
    }
    return minimum;
  }

  @Override
  public float getChildX(int i) {
    if (i >= this.children.size() || i < 0) {
      throw new IndexOutOfBoundsException("UIBoxLayered does not contain " + i + " children.");
    }
    UIElement child = this.children.get(i);
    return this.getX() + Math.max(child.x, this.margin + child.getPadding());
  }

  @Override
  public float getChildY(int i) {
    if (i >= this.children.size() || i < 0) {
      throw new IndexOutOfBoundsException("UIBoxLayered does not contain " + i + " children.");
    }
    UIElement child = this.children.get(i);
    return this.getY() - Math.max(child.y, this.margin + child.getPadding());
  }
}
