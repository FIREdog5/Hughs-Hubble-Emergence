package bin.graphics.ui.complex;

import bin.graphics.ui.UIButton;
import bin.graphics.ui.UIElement;

import bin.graphics.objects.Global;
import bin.graphics.objects.Circle;

import java.lang.UnsupportedOperationException;

public class UITestCircle extends UIButton {
  public float circleRadius;

  private float deltaX;
  private float deltaY;

  public UITestCircle(UIElement parent) {
    super(parent);
    this.circleRadius = 0;
    this.deltaX = 0;
    this.deltaY = 0;
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
  public float getWidth() {
    return this.circleRadius * 2;
  }

  @Override
  public float getHeight() {
    return this.circleRadius * 2;
  }

  @Override
  public void render() {
    if (!this.noBackground) {
      Global.drawColor(this.getColor());
      Circle.draw(this.circleRadius, this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2);
    }
  }

  @Override
  public boolean isMouseOver(float x, float y) {
    return (Math.hypot(this.getX() + this.circleRadius - x, this.getY() - this.circleRadius - y) <= this.circleRadius) && (this.parent == null || this.parent.allowChildContent(x, y));
  }

  @Override
  public void mousedDown(float x, float y) {
    if(!this.getIsMouseDown()) {
      this.setIsMouseDown(true);
      this.deltaX = x - this.getX();
      this.deltaY = y - this.getY();
    }
  }

  @Override
  public void mouseMoved(float x, float y) {
    if (this.getIsMouseDown()) {
      this.x = x - this.deltaX;
      this.y = y - this.deltaY;
    }
  }

  @Override
  public void addChild(UIElement child) {
    throw new UnsupportedOperationException();
  }
}
