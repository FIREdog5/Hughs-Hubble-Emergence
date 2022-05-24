package bin.graphics.ui;

import bin.input.IClickable;

import java.util.ArrayList;

public abstract class UIElement {
  public float x;
  public float y;
  public float minWidth;
  public float maxWidth;
  public float minHeight;
  public float maxHeight;
  public UIElement parent;
  public float margin;
  public float padding;
  public ArrayList<UIElement> children;
  public UIElement() {
    this.children = new ArrayList<UIElement>();
    maxWidth = -1f;
    maxHeight = -1f;
  }
  public abstract float getX();
  public abstract float getY();
  public abstract float getChildX(int i);
  public abstract float getChildY(int i);
  public abstract float getWidth();
  public abstract float getHeight();
  public abstract float getPadding();
  public abstract float getMargin();
  public abstract void render();
  public abstract void cleanUp();
  public void addChild(UIElement child) {
    this.children.add(child);
  }
  public boolean deepContains(UIElement target) {
    if (this.children.contains(target)) {
      return true;
    } else {
      for (UIElement child : this.children) {
        if (child.deepContains(target)) {
          return true;
        }
      }
      return false;
    }
  }
  public boolean deepContains(IClickable target) {
    if (!(target instanceof UIElement)) {
      return false;
    }
    if (this.children.contains((UIElement)target)) {
      return true;
    } else {
      for (UIElement child : this.children) {
        if (child.deepContains((UIElement)target)) {
          return true;
        }
      }
      return false;
    }
  }
}
