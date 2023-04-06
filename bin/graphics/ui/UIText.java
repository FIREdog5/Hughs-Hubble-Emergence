package bin.graphics.ui;

import bin.graphics.objects.Text;

import java.lang.IndexOutOfBoundsException;
import java.lang.UnsupportedOperationException;

public class UIText extends UIBox {

  public String text;
  public float size;

  public UIText(UIElement parent, String text, float size) {
    super(parent);
    this.text = text;
    this.size = size;
  }

  public float getSize() {
    return this.size;
  }

  @Override
  public float getWidth() {
    if (this.text == null || this.text.length() == 0) {
      return 0;
    } else {
      return Text.getWidth(this.text, this.getSize());
    }
  }

  @Override
  public float getHeight() {
    if (this.text == null || this.text.length() == 0) {
      return 0;
    } else {
      return Text.getHeight(this.text, this.getSize());
    }
  }

  @Override
  public float getChildX(int i) {
    throw new java.lang.UnsupportedOperationException();
  }

  @Override
  public float getChildY(int i) {
    throw new java.lang.UnsupportedOperationException();
  }

  @Override
  public void render() {
    Text.draw(this.text, this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getColor(), this.getSize());
  }

  @Override
  public void addChild(UIElement child) {
    throw new java.lang.UnsupportedOperationException();
  }

  @Override
  public void addChild(UIElement child, int index) {
    throw new UnsupportedOperationException();
  }

}
