package bin.graphics.ui.colorUtils;

import bin.graphics.Color;
import bin.graphics.ui.UIButton;
import bin.graphics.ui.UIElement;
import bin.graphics.ui.UIBoxCol;

import bin.graphics.objects.Global;
import bin.graphics.objects.ColorWheel;

import java.lang.UnsupportedOperationException;

public class UIColorWheel extends UIButton {
  public float circleRadius;
  private Color colorData;

  final float indicatorRadius = .25f;

  private UIBoxCol colorIndicator;

  public UIColorWheel(UIElement parent, Color colorData) {
    super(parent);
    this.circleRadius = 0;
    this.colorData = colorData;
    this.colorIndicator = new UIBoxCol(this);
    this.colorIndicator.color = colorData;
    this.colorIndicator.minWidth = this.indicatorRadius * 2;
    this.colorIndicator.minHeight = this.indicatorRadius * 2;
    this.colorIndicator.radius = this.indicatorRadius;
    this.colorIndicator.outlineColor = new Color("#000000");
    this.colorIndicator.outlineWeight = .1f;
    this.children.add(this.colorIndicator);
  }

  private void positionIndicator() {
    float[] hsv = this.colorData.getHSV();
    float hue = hsv[0];
    float saturation = hsv[1];
    this.colorIndicator.x = saturation * (float)Math.cos(hue) * this.circleRadius + this.circleRadius - this.indicatorRadius;
    this.colorIndicator.y = saturation * (float)Math.sin(hue) * this.circleRadius + this.circleRadius - this.indicatorRadius;
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
  public float getChildX(int i) {
    this.positionIndicator();
    return this.getX() + this.children.get(i).x;
  }

  @Override
  public float getChildY(int i) {
    this.positionIndicator();
    return this.getY() - this.children.get(i).y;
  }

  @Override
  public void render() {
    ColorWheel.draw(this.circleRadius, this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2);
    for (int i = 0; i < this.children.size(); i++) {
      this.children.get(i).render();
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
      float deltaX = x - (this.getX() + this.circleRadius);
      float deltaY = y - (this.getY() - this.circleRadius);
      float hue = ((float)Math.PI * 2 - (float)Math.atan2(deltaY, deltaX)) % ((float)Math.PI * 2);
      hue = hue < 0? (((float)Math.PI * 2) + hue) % ((float)Math.PI * 2) : hue;
      float saturation = (float)Math.hypot(deltaX, deltaY) / this.circleRadius;
      this.colorData.setHSV(new float[] {hue, saturation, 1f});
    }
  }

  @Override
  public void mouseMoved(float x, float y) {
    if (this.getIsMouseDown()) {
      float deltaX = x - (this.getX() + this.circleRadius);
      float deltaY = y - (this.getY() - this.circleRadius);
      float hue = ((float)Math.PI * 2 - (float)Math.atan2(deltaY, deltaX)) % ((float)Math.PI * 2);
      hue = hue < 0? (((float)Math.PI * 2) + hue) % ((float)Math.PI * 2) : hue;
      float saturation = Math.min((float)Math.hypot(deltaX, deltaY) / this.circleRadius, 1f);
      this.colorData.setHSV(new float[] {hue, saturation, 1f});
    }
  }

  @Override
  public void addChild(UIElement child) {
    throw new UnsupportedOperationException();
  }
}
