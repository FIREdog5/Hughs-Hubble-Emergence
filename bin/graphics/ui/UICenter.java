package bin.graphics.ui;

import java.lang.IndexOutOfBoundsException;

public class UICenter extends UIBox implements IPositioner{

  public boolean centerX;
  public boolean centerY;

  public UICenter(UIElement parent) {
    super(parent);
    this.centerX = true;
    this.centerY = true;
  }

  public UIElement getRealParent() {
    if (parent instanceof IPositioner) {
      return ((IPositioner)this.parent).getRealParent();
    }
    return this.parent;
  }

  @Override
  public float getWidth() {
    if (this.children.size() > 0) {
      UIElement child = this.children.get(0);
      return child.getWidth();
    } else {
      return 0;
    }
  }

  @Override
  public float getHeight() {
    if (this.children.size() > 0) {
      UIElement child = this.children.get(0);
      return child.getHeight();
    } else {
      return 0;
    }
  }

  @Override
  public float getChildX(int i) {
    if (i >= this.children.size() || i < 0) {
      throw new IndexOutOfBoundsException("UICenter does not contain " + i + " children.");
    }
    UIElement child = this.children.get(0);
    if (this.centerX) {
      return this.getX() + (this.getRealParent().getWidth() - this.getRealParent().getMargin() - (child.getWidth() + child.getPadding() * 2)) / 2 - child.getPadding() / 2;
    } else {
      return this.getX();
    }
  }

  @Override
  public float getChildY(int i) {
    if (i >= this.children.size() || i < 0) {
      throw new IndexOutOfBoundsException("UICenter does not contain " + i + " children.");
    }
    UIElement child = this.children.get(0);
    if (this.centerY) {
      return this.getY() - (this.getRealParent().getHeight() - this.getRealParent().getMargin() - (child.getHeight() + child.getPadding() * 2)) / 2 + child.getPadding() / 2;
    } else {
      return this.getY();
    }
  }

  @Override
  public float getPadding() {
    if (this.children.size() > 0) {
      UIElement child = this.children.get(0);
      return child.getPadding() + this.padding;
    } else {
      return this.padding;
    }
  }

  @Override
  public void render() {
    for (int i = 0; i < this.children.size(); i++) {
      this.children.get(i).render();
    }
  }

  @Override
  public void addChild(UIElement child) {
    if (this.children.size() > 0) {
      this.children.get(0).cleanUp();
      this.children.set(0, child);
    } else {
      this.children.add(child);
    }
  }

}
