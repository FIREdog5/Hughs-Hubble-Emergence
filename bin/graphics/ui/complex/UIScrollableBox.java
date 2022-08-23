package bin.graphics.ui.complex;

import bin.graphics.Color;
import bin.input.IClickable;
import bin.input.ClickHandler;
import bin.graphics.Renderer;

import bin.graphics.objects.Global;
import bin.graphics.objects.Rect;
import bin.graphics.objects.RoundedBox;
import bin.graphics.objects.RoundedBoxOutline;
import bin.graphics.objects.ScreenArea;

import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UIElement;

public class UIScrollableBox extends UIBoxCol implements IClickable {
  private ClickHandler clickHandler;
  private boolean isMousedOver;
  private boolean isMouseDown;
  private boolean isDead;
  //set z to -1 to be ignored
  private int z;
  public Color scrollbarColor;
  public Color scrollbarMouseOverColor;
  public float scrollbarWidth;

  private float scroll;
  private float deltaY;

  private int fbo;

  public UIScrollableBox(UIElement parent) {
    super(parent);
    this.isMousedOver = false;
    this.scrollbarColor = null;
    this.scrollbarMouseOverColor = null;
    this.scrollbarWidth = 0f;
    this.z = 0;
    this.isDead = false;
    this.maxHeight = 0;
    this.scroll = 0;
    this.deltaY = 0;
    this.fbo = -1;
  }

  @Override
  public float getWidth() {
    return super.getWidth() + this.scrollbarWidth + this.getOutlineWeight() * 2;
  }

  @Override
  public float getHeight() {
    return this.maxHeight;
  }

  private float getScrollBarMaxHeight() {
    return this.maxHeight - this.getOutlineWeight() * 2;
  }

  public float getScroll() {
    return this.scroll;
  }

  public void setScroll(float scroll) {
    this.scroll = scroll;
    this.capScroll();
  }

  public Color getScrollbarColor() {
    if (this.scrollbarColor == null) {
      return new Color("#ffffff");
    }
    return this.scrollbarColor;
  }
  public Color getScrollbarMouseOverColor() {
    if (this.scrollbarMouseOverColor == null) {
      return new Color("#ffffff");
    }
    return this.scrollbarMouseOverColor;
  }
  public float getScrollbarWidth() {
    return this.scrollbarWidth;
  }

  public float getChildHeight() {
    float minimum = 0;
    if (this.children.size() == 0) {
      return minimum;
    }

    float sum = this.getMargin();
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

    minimum = Math.max(minimum, sum + child.getPadding() + this.getMargin() + child.getHeight());
    return minimum;
  }

  @Override
  public boolean getIsMousedOver() {
    return this.isMousedOver;
  }

  public void setIsMousedOver(boolean isMousedOver){
    this.isMousedOver = isMousedOver;
  }

  public boolean getIsMouseDown() {
    return this.isMouseDown;
  }

  protected void setIsMouseDown(boolean isMouseDown){
    this.isMouseDown = isMouseDown;
  }

  protected void capScroll() {
    this.scroll = Math.max(0, Math.min(scroll, this.getChildHeight() - this.getHeight()));
  }

  @Override
  public void render() {
    if (this.fbo == -1) {
      this.fbo = Renderer.framebufferController.createNewFrame();
    }
    this.capScroll();
    if (!this.getNoBackground()) {
      Global.drawColor(this.getColor());
      RoundedBox.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight(), 0f);
    }
    if (this.getOutlineWeight() > 0f) {
      Global.drawColor(this.getOutlineColor());
      RoundedBoxOutline.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight(), 0f, this.getOutlineWeight());
    }
    //draw scroll bar
    if (this.getScrollbarWidth() > 0) {
      Global.drawColor(this.getOutlineColor());
      Rect.draw(this.getX() + this.getWidth() - this.getScrollbarWidth() - this.getOutlineWeight(), this.getY() - this.getHeight() / 2, .03f, this.getHeight() - this.getOutlineWeight() * 2);
      Global.drawColor(this.getIsMousedOver() ? this.getScrollbarMouseOverColor() : this.getScrollbarColor());
      Rect.draw(this.getX() + this.getWidth() - this.getScrollbarWidth() / 2 - this.getOutlineWeight(), this.getY() - Math.min(this.getHeight() / this.getChildHeight(), 1f) * this.getScrollBarMaxHeight() / 2 - (this.scroll / this.getChildHeight()) * this.getScrollBarMaxHeight() - this.getOutlineWeight(), this.getScrollbarWidth(), Math.min(this.getHeight() / this.getChildHeight(), 1f) * this.getScrollBarMaxHeight());
    }

    //switch to fbo
    Renderer.framebufferController.switchToFrame(this.fbo);
    //draw children
    for (int i = 0; i < this.children.size(); i++) {
      this.children.get(i).render();
    }
    //switch back to previous (or default) fbo
    Renderer.framebufferController.popFrameAndBind();
    //draw fbo content
    ScreenArea.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth() - 2 * this.getOutlineWeight(), this.getHeight() - 2 * this.getOutlineWeight());
  }

  @Override
  public float getChildX(int i) {
    return super.getChildX(i) + this.getOutlineWeight();
  }

  @Override
  public float getChildY(int i) {
    if (i >= this.children.size() || i < 0) {
      throw new IndexOutOfBoundsException("UIBoxCol does not contain " + i + " children.");
    }
    float sum = this.getMargin();
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
    return this.getY() - sum + ((this.getChildHeight() != 0 ? this.scroll / this.getChildHeight() : 0) * (this.getChildHeight() + this.getOutlineWeight() * 3f)) - this.getOutlineWeight();
  }

  @Override
  public int getZ(){
    //TODO: solution for nested scrollyboiz.. probably need an isScrollable method in IClickable.
    return -1;
  }

  @Override
  public void setZ(int z){
    this.z = z;
  }

  @Override
  public void setClickHandler(ClickHandler clickHandler) {
    this.clickHandler = clickHandler;
  }

  @Override
  public ClickHandler getClickHandler() {
    return this.clickHandler;
  }

  @Override
  public boolean isMouseOver(float x, float y) {
    if (this.isDead) {
      return false;
    }
    return x >= this.getX() && x <= this.getX() + this.getWidth() && y <= this.getY() && y >= this.getY() - this.getHeight() && (this.parent == null || this.parent.allowChildContent(x, y));
  }

  public boolean isMouseOverScrollbar(float x, float y) {
    if (this.isDead || this.scrollbarWidth == 0f) {
      return false;
    }
    return x >= this.getX() + this.getWidth() - this.getOutlineWeight() - this.scrollbarWidth && x <= this.getX() + this.getWidth() - this.getOutlineWeight() && y <= this.getY() - this.getOutlineWeight() && y >= this.getY() - this.getHeight() + this.getOutlineWeight();
  }

  @Override
  public void cleanFromClickHandler() {
    this.getClickHandler().remove(this);
  }

  @Override
  public void clickedOn(float x, float y) {
    //can't be clicked
    return;
  }

  @Override
  public void mousedOver(float x, float y) {
    if (this.isDead) {
      return;
    }
    if (this.isMouseOverScrollbar(x, y)) {
      this.isMousedOver = true;
    }
  }

  @Override
  public void mousedOff(float x, float y) {
    if (this.isDead) {
      return;
    }
    this.isMousedOver = false;
  }

  @Override
  public void mousedDown(float x, float y) {
    if (this.isDead) {
      return;
    }
    if (this.isMouseOverScrollbar(x, y)) {
      this.isMouseDown = true;
      this.deltaY = y - this.getY() + Math.min(this.getHeight() / this.getChildHeight(), 1f) * this.getScrollBarMaxHeight() / 2 + (this.scroll / this.getChildHeight()) * this.getScrollBarMaxHeight() + this.getOutlineWeight();
      if (this.deltaY > Math.min(this.getHeight() / this.getChildHeight(), 1f) * this.getScrollBarMaxHeight() / 2) {
        this.deltaY = Math.min(this.getHeight() / this.getChildHeight(), 1f) * this.getScrollBarMaxHeight() / 2;
      }
      if (this.deltaY < -Math.min(this.getHeight() / this.getChildHeight(), 1f) * this.getScrollBarMaxHeight() / 2) {
        this.deltaY = -Math.min(this.getHeight() / this.getChildHeight(), 1f) * this.getScrollBarMaxHeight() / 2;
      }
      this.scroll = -(y + this.getOutlineWeight() + Math.min(this.getHeight() / this.getChildHeight(), 1f) * this.getScrollBarMaxHeight() / 2 - this.getY() - this.deltaY) * this.getChildHeight() / this.getScrollBarMaxHeight();
    }
  }

  @Override
  public void mousedUp(float x, float y) {
    if (this.isDead) {
      return;
    }
    this.isMouseDown = false;
  }

  @Override
  public void scrolledOver(float x, float y, float ammount) {
    if (this.isDead) {
      return;
    }
    this.scroll -= ammount;
    this.capScroll();
  }

  @Override
  public void mouseMoved(float x, float y) {
    if (this.isDead) {
      return;
    }
    if (this.isMouseOverScrollbar(x, y)) {
      this.isMousedOver = true;
    } else {
      this.isMousedOver = false;
    }
    if (this.getIsMouseDown()) {
      this.scroll = -(y + this.getOutlineWeight() + Math.min(this.getHeight() / this.getChildHeight(), 1f) * this.getScrollBarMaxHeight() / 2 - this.getY() - this.deltaY) * this.getChildHeight() / this.getScrollBarMaxHeight();
    }
  }

  @Override
  public void cleanUp() {
    this.isDead = true;
    this.cleanFromClickHandler();
    if (this.fbo != -1) {
      Renderer.framebufferController.cleanUpFrame(this.fbo);
    }
    super.cleanUp();
  }

  @Override
  public boolean allowChildContent(float x, float y) {
    return this.isMouseOver(x, y);
  }

}
