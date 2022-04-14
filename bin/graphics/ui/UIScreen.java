package bin.graphics.ui;

import bin.graphics.Renderer;

import java.lang.IndexOutOfBoundsException;

public class UIScreen extends UIElement {
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
    for (int i = 0; i < this.children.size(); i++) {
      UIElement child = this.children.get(i);
      child.parent = null;
      child.cleanUp();
    }
    this.children = null;
  }

}
