package bin.graphics.ui;

import java.lang.IndexOutOfBoundsException;

public class UIRightPositioner extends UIBoxRow implements IPositioner{
  public UIRightPositioner(UIElement parent) {
    super(parent);
  }

  public UIElement getRealParent() {
    if (parent instanceof IPositioner) {
      return ((IPositioner)this.parent).getRealParent();
    }
    return this.parent;
  }

  @Override
  public float getChildX(int i) {
    if (i >= this.children.size() || i < 0) {
      throw new IndexOutOfBoundsException("UICenter does not contain " + i + " children.");
    }
    UIElement child = this.children.get(0);
    return this.getRealParent().getX() + this.x + this.getRealParent().getWidth() - child.getWidth();
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
