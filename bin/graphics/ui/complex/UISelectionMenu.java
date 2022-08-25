package bin.graphics.ui.complex;

import bin.input.IClickable;
import bin.graphics.ui.UISelectable;
import bin.graphics.ui.UIElement;
import bin.graphics.Color;
import bin.graphics.objects.Global;
import bin.graphics.objects.RoundedBox;
import bin.graphics.objects.RoundedBoxOutline;
import bin.graphics.objects.ScreenArea;
import bin.graphics.Renderer;
import bin.input.ClickHandler;

import java.util.ArrayList;
import java.lang.UnsupportedOperationException;

public abstract class UISelectionMenu extends UISelectable {

  public float selectionBoxHeight;
  public Color scrollbarColor;
  public Color scrollbarMouseOverColor;
  public float scrollbarWidth;
  private ArrayList<String> options;

  public UIScrollableBox scrollableList;

  private int fbo;

  public UISelectionMenu(UIElement parent, UIElement container, ClickHandler clickHandler) {
    super(parent);

    UISelectionMenu ref = this;

    this.selectionBoxHeight = 0f;
    this.options = new ArrayList<String>();
    this.scrollbarColor = null;
    this.scrollbarMouseOverColor = null;
    this.scrollbarWidth = 0f;

    this.fbo = -1;

    this.scrollableList = new UIScrollableBox(container) {
      @Override
      public float getMargin() {
        return 0;
      }

      @Override
      public float getX() {
        return ref.getX();
      }

      @Override
      public float getY() {
        return ref.getY() - ref.getHeight() + ref.getOutlineWeight();
      }

      @Override
      public Color getScrollbarColor() {
        if (ref.scrollbarColor == null) {
          return new Color("#ffffff");
        }
        return ref.scrollbarColor;
      }

      @Override
      public Color getScrollbarMouseOverColor() {
        if (ref.scrollbarMouseOverColor == null) {
          return new Color("#ffffff");
        }
        return ref.scrollbarMouseOverColor;
      }

      @Override
      public float getScrollbarWidth() {
        return ref.scrollbarWidth;
      }

      @Override
      public Color getOutlineColor() {
        return ref.getOutlineColor();
      }

      @Override
      public Color getColor() {
        return ref.getColor();
      }

      @Override
      public float getOutlineWeight() {
        return ref.getOutlineWeight();
      }

      @Override
      public float getHeight() {
        return Math.min(this.getChildHeight(), ref.selectionBoxHeight);
      }

      @Override
      public void render() {
        if (ref.isSelected()) {
          super.render();
        }
      }

      @Override
      public boolean allowChildContent(float x, float y) {
        return ref.allowChildContent(x, y) && super.allowChildContent(x, y);
      }

      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        int c = 0;
        for (UIElement child: this.children) {
          if (((UISelectable) child).isMouseOver(x, y)) {
            ref.setValue(ref.options.get(c));
            ref.deselect();
            return;
          }
          c++;
        }
        ref.blockDeselect();
      }
    };

    this.scrollableList.setZ(9);
    container.addChild(this.scrollableList);
    clickHandler.register(this.scrollableList);

  }

  @Override
  public float getWidth() {
    return Math.max(this.minWidth + this.getMargin() * 2, this.scrollableList.getWidth());
  }

  @Override
  public float getHeight() {
    if (this.scrollableList.children.size() == 0) {
      float minimum = Math.max(this.minHeight, this.margin * 2);
      if (this.maxHeight != -1) {
        minimum = Math.min(minimum, maxHeight);
      }
      return minimum;
    }
    if (this.getValue() == null || this.getValue().equals("") || this.options.indexOf(this.getValue()) == -1) {
      return this.scrollableList.children.get(0).getHeight() + Math.max(2f * this.scrollableList.children.get(0).getPadding(), 2f * this.getMargin());
    }
    int i = this.options.indexOf(this.getValue());
    return this.scrollableList.children.get(i).getHeight() + Math.max(2f * this.scrollableList.children.get(i).getPadding(), 2f * this.getMargin());
  }

  public abstract String getValue();
  public abstract void setValue(String val);

  @Override
  public void render() {
    if (this.fbo == -1) {
      this.fbo = Renderer.framebufferController.createNewFrame();
    }

    this.deselectBlocked = false;
    if (!this.getNoBackground()) {
      Global.drawColor(this.getColor());
      RoundedBox.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, (this.getWidth() + (this.getIsMouseDown() ? .2f : 0)), (this.getHeight() + (this.getIsMouseDown() ? .2f : 0)), this.getRadius());
    }
    if (this.getOutlineWeight() > 0f) {
      Global.drawColor(this.getOutlineColor());
      RoundedBoxOutline.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, (this.getWidth() + (this.getIsMouseDown() ? .2f : 0)), (this.getHeight() + (this.getIsMouseDown() ? .2f : 0)), this.getRadius(), this.getOutlineWeight());
    }

    if (!(this.getValue() == null || this.getValue().equals("") || this.options.indexOf(this.getValue()) == -1)){
      //switch to fbo
      Renderer.framebufferController.switchToFrame(this.fbo, true);
      //draw selected child
      int i = this.options.indexOf(this.getValue());
      boolean tempMousedOver = ((UISelectable)this.scrollableList.children.get(i)).getIsMousedOver();
      boolean tempNoBackground = ((UISelectable)this.scrollableList.children.get(i)).noBackground;
      float tempScroll = this.scrollableList.getScroll();
      ((UISelectable)this.scrollableList.children.get(i)).noBackground = true;
      ((UISelectable)this.scrollableList.children.get(i)).setIsMousedOver(false);
      this.scrollableList.setScroll(this.scrollableList.getY() - this.scrollableList.children.get(i).getY() + this.scrollableList.getScroll());
      this.scrollableList.children.get(i).render();
      //switch back to previous (or default) fbo
      Renderer.framebufferController.popFrameAndBind();
      //draw fbo content displaced
      ScreenArea.draw(this.scrollableList.children.get(i).getX() + this.getWidth() / 2f, this.scrollableList.children.get(i).getY() - this.getHeight() / 2f, this.getX() + this.getWidth() / 2f + this.getMargin(), this.getY() - this.getHeight() / 2f - this.getMargin(), this.getWidth(), this.getHeight());
      ((UISelectable)this.scrollableList.children.get(i)).setIsMousedOver(tempMousedOver);
      ((UISelectable)this.scrollableList.children.get(i)).noBackground = tempNoBackground;
      this.scrollableList.setScroll(tempScroll);
    }
  }

  @Override
  public boolean deepContains(UIElement target) {
    if (scrollableList.deepContains(target)) {
      return true;
    }
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

  @Override
  public boolean deepContains(IClickable target) {
    if (scrollableList.deepContains(target)) {
      return true;
    }
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

  @Override
  public boolean allowChildContent(float x, float y) {
    return this.isSelected() && super.allowChildContent(x, y);
  }

  public void addOption(UISelectable optionObject, String optionString) {
    options.add(optionString);
    this.scrollableList.addChild(optionObject);
  }

  @Override
  public void addChild(UIElement child) {
    throw(new UnsupportedOperationException());
  }

  @Override
  public void cleanUp() {
    if (this.children != null) {
      this.removeChildren();
      this.children = null;
    }
    if (this.scrollableList != null) {
      this.scrollableList.removeFromParent();
      this.scrollableList = null;
    }
  }

}
