package bin.graphics.ui.complex;

import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UIElement;
import bin.graphics.Renderer;

public class UIPopUp extends UIBoxCol {

  public UIElement anchor;
  public float offset;
  public String position;

  public UIPopUp(UIElement parent) {
    super(parent);
    this.anchor = null;
    this.offset = 0;
    this.position = "top";
  }

  @Override
  public float getX() {
    if (this.anchor == null) {
      return 0;
    }
    switch(this.calcPosition()) {
      case "top":
      case "bottom":
        return this.anchor.getX() + this.anchor.getWidth() / 2 - this.getWidth() / 2;
      case "right":
        return this.anchor.getX() + this.anchor.getWidth() + this.offset;
      case "left":
        return this.anchor.getX() - this.getWidth() - this.offset;
    }
    return 0;
  }

  @Override
  public float getY() {
    if (this.anchor == null) {
      return 0;
    }
    switch(this.calcPosition()) {
      case "top":
        return this.anchor.getY() + this.getHeight() + this.offset;
      case "bottom":
        return this.anchor.getY() - this.anchor.getHeight() - this.offset;
      case "left":
      case "right":
        return this.anchor.getY() - this.anchor.getHeight() / 2 + this.getHeight() / 2;
    }
    return 0;
  }

  private String calcPosition() {
    String[] order = {"top", "bottom", "right", "left"};
    if (this.willFit(this.position)) {
      return this.position;
    }
    for (String pos : order) {
      if (pos.equals(this.position)) {
        continue;
      }
      if (this.willFit(pos)) {
        return pos;
      }
    }
    return this.position;
  }

  private boolean willFit(String position) {
    float maxX = Renderer.unitsWide;
    float maxY = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    float checkX;
    float checkY;
    switch(position) {
      case "top":
        checkY = anchor.getY();
        checkX = anchor.getX() + anchor.getWidth() / 2;
        if (checkY + this.getHeight() + offset <= maxY && checkX + this.getWidth() / 2 <= maxX && checkX - this.getWidth() / 2 >= -maxX) {
          return true;
        } else {
          return false;
        }
      case "bottom":
        checkY = anchor.getY() - anchor.getHeight();
        checkX = anchor.getX() + anchor.getWidth() / 2;
        if (checkY - this.getHeight() - offset >= -maxY && checkX + this.getWidth() / 2 <= maxX && checkX - this.getWidth() / 2 >= -maxX) {
          return true;
        } else {
          return false;
        }
      case "right":
        checkY = anchor.getY() - anchor.getHeight() / 2;
        checkX = anchor.getX() + anchor.getWidth();
        if (checkX + this.getWidth() + offset <= maxX && checkY + this.getHeight() / 2 <= maxY && checkY - this.getHeight() / 2 >= -maxY) {
          return true;
        } else {
          return false;
        }
      case "left":
        checkY = anchor.getY() - anchor.getHeight() / 2;
        checkX = anchor.getX();
        if (checkX - this.getWidth() - offset >= -maxX && checkY + this.getHeight() / 2 <= maxY && checkY - this.getHeight() / 2 >= -maxY) {
          return true;
        } else {
          return false;
        }
      default:
        return false;
    }
  }

  // private float neededSlide(String position) {
  //   // do this next..
  // }
}
