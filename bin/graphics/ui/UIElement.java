package bin.graphics.ui;

import bin.input.IClickable;

import java.util.ArrayList;
import java.lang.AssertionError;

import java.lang.annotation.*;
import java.lang.reflect.Field;

public abstract class UIElement {
  @CacheSensitive
  public float x;
  @CacheSensitive
  public float y;
  @CacheSensitive
  public float minWidth;
  @CacheSensitive
  public float maxWidth;
  @CacheSensitive
  public float minHeight;
  @CacheSensitive
  public float maxHeight;
  @CacheSensitive
  public UIElement parent;
  @CacheSensitive
  public float margin;
  @CacheSensitive
  public float padding;
  @CacheSensitive
  public ArrayList<UIElement> children;
  public String defInfo;
  public UIElement() {
    this.children = new ArrayList<UIElement>();
    maxWidth = -1f;
    maxHeight = -1f;
    try {
      StackTraceElement[] stackTrace = (new Exception("temp")).getStackTrace();
      for (int i = 0; i < stackTrace.length; i++) {
        // System.out.println(stackTrace[i]);
        if (!stackTrace[i].getClassName().contains("bin.graphics.ui") && !stackTrace[i].getClassName().contains("$")) {
          this.defInfo = stackTrace[i].toString().split("\\(")[1].split("\\)")[0];
          break;
        }
      }
    } catch (Exception e) {
      this.defInfo = "unknown";
    }
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
    this.addChild(child, -1);
  }

  public void addChild(UIElement child, int index) {
    if (child.parent != this) {
      System.out.println("found:");
      System.out.println(child.parent + " (" + child.parent.defInfo + ")");
      System.out.println("which does not match:");
      System.out.println(this + " (" + this.defInfo + ")");
      throw new AssertionError("the child being added does not have the correct parent");
    }
    if (index >= 0 && index < this.children.size()) {
      this.children.add(index, child);
    } else {
      this.children.add(child);
    }
  }

  public boolean deepContains(UIElement target) {
    if (this.equals(target)) {
      return true;
    }
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
    if (this.equals((UIElement)target)) {
      return true;
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
  public boolean allowChildContent(float x, float y) {
    if (parent == null) {
      return true;
    }
    return parent.allowChildContent(x,y);
  }

  @Target({ ElementType.FIELD })
  @Retention(RetentionPolicy.RUNTIME)
  protected @interface CacheSensitive {}
}
