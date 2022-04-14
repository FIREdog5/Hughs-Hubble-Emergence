package bin.graphics.ui;

import java.lang.IndexOutOfBoundsException;

public class UIBoxCol extends UIBox {

  public UIBoxCol(UIElement parent) {
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
    if (this.children.size() == 0) {
      return minimum;
    }

    float sum = this.margin;
    UIElement child = null;
    for (int index = 0; index < this.children.size(); index++) {
      UIElement newChild = this.children.get(index);
      if (child != null) {
        sum += Math.max(child.getPadding(), newChild.getPadding()) + child.getHeight();
      } else {
        sum += newChild.getPadding();
      }
      child = newChild;
      sum = Math.max(child.y, sum);
    }

    minimum = Math.max(minimum, sum + child.getPadding() + this.margin + child.getHeight());
    if (this.maxHeight != -1) {
      minimum = Math.min(minimum, maxHeight);
    }
    return minimum;
  }

  @Override
  public float getChildX(int i) {
    if (i >= this.children.size() || i < 0) {
      throw new IndexOutOfBoundsException("UIBoxCol does not contain " + i + " children.");
    }
    UIElement child = this.children.get(i);
    return this.getX() + Math.max(child.x, this.margin + child.getPadding());
  }

  @Override
  public float getChildY(int i) {
    if (i >= this.children.size() || i < 0) {
      throw new IndexOutOfBoundsException("UIBoxCol does not contain " + i + " children.");
    }
    float sum = this.margin;
    UIElement child = null;
    for (int index = 0; index <= i; index++) {
      UIElement newChild = this.children.get(index);
      if (child != null) {
        sum += Math.max(child.getPadding(), newChild.getPadding()) + child.getHeight();
      } else {
        sum += newChild.getPadding();
      }
      child = newChild;
      sum = Math.max(child.y, sum);
    }
    return this.getY() - sum;
  }
}
