package bin.graphics.ui.complex;

import bin.graphics.Renderer;
import bin.graphics.Color;

import bin.graphics.ui.UIBox;
import bin.graphics.ui.UIElement;
import bin.graphics.ui.UICenter;
import bin.graphics.ui.UIBoxCol;

public class UIModal extends UIBox {

  private UICenter centerer;
  public UIBoxCol centerBox;
  public UIBoxCol backgroundBox;

  public UIModal(UIElement parent) {
    super(parent);
    this.backgroundBox = new UIBoxCol(this);
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    backgroundBox.minWidth = Renderer.unitsWide;
    backgroundBox.minHeight = unitsTall;
    backgroundBox.color = new Color("#404040", .5f);
    backgroundBox.outlineWeight = 0;
    this.children.add(backgroundBox);
    this.centerer = new UICenter(this);
    this.children.add(centerer);
    this.centerBox = new UIBoxCol(centerer);
    centerer.addChild(this.centerBox);
  }

  @Override
  public float getWidth() {
    return Renderer.unitsWide;
  }

  @Override
  public float getHeight() {
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    return unitsTall;
  }

  @Override
  public float getChildX(int i) {
    return -Renderer.unitsWide / 2f;
  }

  @Override
  public float getChildY(int i) {
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    return unitsTall / 2f;
  }

  @Override
  public void render() {
    for (int i = 0; i < this.children.size(); i++) {
      this.children.get(i).render();
    }
  }

  public void close() {
    this.removeFromParent();
  }

  @Override
  public void addChild(UIElement child) {
    this.addChild(child, -1);
  }

  @Override
  public void addChild(UIElement child, int index) {
    this.centerBox.addChild(child, index);
  }

}
