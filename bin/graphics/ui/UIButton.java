package bin.graphics.ui;

import bin.graphics.Color;
import bin.input.IClickable;
import bin.input.ClickHandler;

import bin.graphics.objects.Global;
import bin.graphics.objects.RoundedBox;
import bin.graphics.objects.RoundedBoxOutline;


public class UIButton extends UIBoxCol implements IClickable {
  private ClickHandler clickHandler;
  private boolean isMousedOver;
  private boolean isMouseDown;
  //set z to -1 to be ignored
  private int z;
  public Color mouseOverColor;
  public Color mouseOverOutlineColor;

  public UIButton(UIElement parent) {
    super(parent);
    this.isMousedOver = false;
    this.mouseOverColor = null;
    this.mouseOverOutlineColor = null;
    this.z = 0;
  }

  @Override
  public Color getColor() {
    if (this.isMousedOver && this.mouseOverColor != null) {
      return this.mouseOverColor;
    } else if (this.color != null) {
      return this.color;
    } else if (this.parent instanceof IColorable) {
      return ((IColorable)this.parent).getColor();
    } else {
      return new Color("#ffffff");
    }
  }

  @Override
  public Color getOutlineColor() {
    if (this.isMousedOver && this.mouseOverOutlineColor != null) {
      return this.mouseOverOutlineColor;
    } else if (this.outlineColor != null) {
      return this.outlineColor;
    } else if (this.parent instanceof IOutlineable) {
      return ((IOutlineable)this.parent).getOutlineColor();
    } else {
      return new Color("#ffffff");
    }
  }

  // @Override
  // public float getWidth(){
  //   return super.getWidth() - (this.isMouseDown ? .5f : 0);
  // }
  //
  // @Override
  // public float getHeight(){
  //   return super.getHeight() - (this.isMouseDown ? .5f : 0);
  // }

  @Override
  public void render() {
    if (!this.noBackground) {
      Global.drawColor(this.getColor());
      RoundedBox.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, (this.getWidth() + (this.isMouseDown ? .2f : 0)), (this.getHeight() + (this.isMouseDown ? .2f : 0)), this.radius);
    }
    if (this.outlineWeight > 0f) {
      Global.drawColor(this.getOutlineColor());
      RoundedBoxOutline.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, (this.getWidth() + (this.isMouseDown ? .2f : 0)), (this.getHeight() + (this.isMouseDown ? .2f : 0)), this.radius, this.outlineWeight);
    }
    for (int i = 0; i < this.children.size(); i++) {
      this.children.get(i).render();
    }
  }
  @Override
  public int getZ(){
    return this.z;
  }

  @Override
  public void setZ(int z){
    this.z = z;
  }

  @Override
  public boolean getIsMousedOver() {
    return this.isMousedOver;
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
    return x >= this.getX() && x <= this.getX() + this.getWidth() && y <= this.getY() && y >= this.getY() - this.getHeight();
  }

  @Override
  public void cleanFromClickHandler() {
    this.getClickHandler().remove(this);
  }

  @Override
  public void clickedOn(float x, float y) {
    //placeholder
    return;
  }

  @Override
  public void mousedOver(float x, float y) {
    this.isMousedOver = true;
  }

  @Override
  public void mousedOff(float x, float y) {
    this.isMousedOver = false;
  }

  @Override
  public void mousedDown(float x, float y) {
    this.isMouseDown = true;
  }

  @Override
  public void mousedUp(float x, float y) {
    this.isMouseDown = false;
  }

  @Override
  public void scrolledOver(float x, float y, float ammount) {
    //placeholder
    return;
  }

  @Override
  public void cleanUp() {
    this.cleanFromClickHandler();
    super.cleanUp();
  }
}
