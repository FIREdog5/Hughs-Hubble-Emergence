package bin.graphics.ui;

import java.lang.IndexOutOfBoundsException;

public class UIBoxRow extends UIBox {

  public UIBoxRow(UIElement parent) {
    super(parent);
  }

  @Override
  public float getWidth() {
    float minimum = Math.max(this.minWidth, this.margin * 2);
    if (this.children.size() == 0) {
      return minimum;
    }

    float sum = this.margin;
    UIElement child = null;
    for (int index = 0; index < this.children.size(); index++) {
      UIElement newChild = this.children.get(index);
      if (child != null) {
        sum += Math.max(child.getPadding(), newChild.getPadding()) + child.getWidth();
      } else {
        sum += newChild.getPadding();
      }
      child = newChild;
      sum = Math.max(child.x, sum);
    }

    minimum = Math.max(minimum, Math.max(child.getX() - this.getX(), 0) + child.getPadding() + this.margin + child.getWidth());
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
      throw new IndexOutOfBoundsException("UIBoxRow does not contain " + i + " children.");
    }
    float sum = this.margin;
    UIElement child = null;
    for (int index = 0; index <= i; index++) {
      UIElement newChild = this.children.get(index);
      if (child != null) {
        sum += Math.max(child.getPadding(), newChild.getPadding()) + child.getWidth();
      } else {
        sum += newChild.getPadding();
      }
      child = newChild;
      sum = Math.max(child.x, sum);
    }
    return this.getX() + sum;
  }

  @Override
  public float getChildY(int i) {
    if (i >= this.children.size() || i < 0) {
      throw new IndexOutOfBoundsException("UIBoxRow does not contain " + i + " children.");
    }
    UIElement child = this.children.get(i);
    return this.getY() - Math.max(child.y, this.margin + child.getPadding());
  }
}
