package bin.graphics.ui;

import bin.graphics.Color;

import bin.graphics.objects.Global;
import bin.graphics.objects.RoundedBox;
import bin.graphics.objects.RoundedBoxOutline;

public class UISelectable extends UIButton {

  //TODO: add tab targets

  private boolean isActive;
  public Color selectedColor;
  public Color selectedOutlineColor;
  protected boolean deselectBlocked;

  public UISelectable(UIElement parent) {
    super(parent);
    this.isActive = false;
    this.selectedColor = null;
    this.selectedOutlineColor = null;
    this.deselectBlocked = false;
  }

  @Override
  public Color getColor() {
    if (this.isActive && this.selectedColor != null) {
      return this.selectedColor;
    } else if (this.getIsMousedOver() && this.mouseOverColor != null) {
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
    if (this.isActive && this.selectedOutlineColor != null) {
      return this.selectedOutlineColor;
    } else if (this.getIsMousedOver() && this.mouseOverOutlineColor != null) {
      return this.mouseOverOutlineColor;
    } else if (this.outlineColor != null) {
      return this.outlineColor;
    } else if (this.parent instanceof IOutlineable) {
      return ((IOutlineable)this.parent).getOutlineColor();
    } else {
      return new Color("#ffffff");
    }
  }

  @Override
  public void render() {
    this.deselectBlocked = false;
    if (!this.noBackground) {
      Global.drawColor(this.getColor());
      RoundedBox.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, (this.getWidth() + (this.getIsMouseDown() ? .2f : 0)), (this.getHeight() + (this.getIsMouseDown() ? .2f : 0)), this.radius);
    }
    if (this.outlineWeight > 0f) {
      Global.drawColor(this.getOutlineColor());
      RoundedBoxOutline.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, (this.getWidth() + (this.getIsMouseDown() ? .2f : 0)), (this.getHeight() + (this.getIsMouseDown() ? .2f : 0)), this.radius, this.outlineWeight);
    }
    for (int i = 0; i < this.children.size(); i++) {
      this.children.get(i).render();
    }
  }

  public void select() {
    this.isActive = true;
  }

  public void blockDeselect() {
    this.deselectBlocked = true;
  }

  public void deselect() {
    if (this.deselectBlocked) {
      return;
    }
    this.isActive = false;
    this.onDeselect();
  }

  public void onDeselect() {
    return;
  }

  public boolean isSelected() {
    return this.isActive;
  }

  @Override
  public void mousedUp(float x, float y) {
    super.mousedUp(x, y);
    if (this.isMouseOver(x, y)) {
      this.select();
    }
  }

}
