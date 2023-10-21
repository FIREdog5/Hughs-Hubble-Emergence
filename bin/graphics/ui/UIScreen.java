package bin.graphics.ui;

import bin.graphics.Renderer;
import bin.input.IClickable;

import java.util.Comparator;
import java.util.Collections;
import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;

public class UIScreen extends UIElement {

  private static final Comparator<UIElement> comparator = (UIElement o1, UIElement o2) -> (o1 instanceof IClickable && o2 instanceof IClickable) ?
                                                          (((IClickable)o1).getZ() < 0 ? -1 : ((IClickable)o2).getZ() < 0 ? 1 : ((IClickable)o2).getZ() - ((IClickable)o1).getZ()) :
                                                          (o1 instanceof IClickable && ((IClickable)o1).getZ() != 0) ? 1 :
                                                          (o2 instanceof IClickable && ((IClickable)o2).getZ() != 0) ? -1 : 0;

  public UIScreen() {
    super();
    this.minWidth = (float)Renderer.unitsWide;
    this.maxWidth = minWidth;
    this.minHeight = (float)((float)Renderer.screenHeight / (float)Renderer.screenWidth) * (float)Renderer.unitsWide;
    this.maxHeight = minHeight;
    this.x = -maxWidth / 2;
    this.y = maxHeight / 2;
    this.parent = null;
  }

  @Override
  public float getX() {
    return this.x;
  }

  @Override
  public float getY() {
    return this.y;
  }

  @Override
  public float getChildX(int i) {
    return this.x + this.children.get(i).x;
  }

  @Override
  public float getChildY(int i) {
    return this.y - this.children.get(i).y;
  }

  @Override
  public float getWidth() {
    return this.maxWidth;
  }

  @Override
  public float getHeight() {
    return this.maxHeight;
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
    for (int i = 0; i < this.children.size(); i++) {
      this.children.get(i).render();
    }
  }

  @Override
  public void cleanUp() {
    if (this.children == null) {
      return;
    }
    for (int i = 0; i < this.children.size(); i++) {
      UIElement child = this.children.get(i);
      child.parent = null;
      child.cleanUp();
    }
    this.children = new ArrayList<UIElement>();
  }

  @Override
  public boolean allowChildContent(float x, float y) {
    return true;
  }

  @Override
  public void addChild(UIElement child) {
    this.addChild(child, -1);
  }

  @Override
  public void addChild(UIElement child, int index) {
    super.addChild(child, index);
    Collections.sort(this.children, comparator);
  }

}
