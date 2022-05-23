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
    return Math.hypot(this.getX() - x, this.getY() - y) <= this.circleRadius;
  }

  @Override
  public void mousedDown(float x, float y) {
    // this.isMouseDown = true;
    this.deltaX = x - this.getX() + radius;
    this.deltaX = y - this.getY() - radius;
  }

  @Override
  public void mousedOver(float x, float y) {
    super.mousedOver(x, y);
    // if (this.isMouseDown) {
    //   this.x = x - this.deltaX;
    //   this.x = y - this.deltaY;
    // }
  }

  @Override
  public void mousedOff(float x, float y) {
    super.mousedOff(x, y);
    // if (this.isMouseDown) {
    //   this.x = x - this.deltaX;
    //   this.x = y - this.deltaY;
    // }
  }

  @Override
  public void addChild(UIElement child) {
    throw new UnsupportedOperationException();
  }
}
