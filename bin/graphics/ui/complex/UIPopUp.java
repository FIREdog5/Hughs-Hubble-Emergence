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

  public float getOffset() {
    return this.offset;
  }

  public UIElement getAnchor() {
    return this.anchor;
  }

  public String getPosition() {
    return this.position;
  }

  @Override
  public float getX() {
    if (this.getAnchor() == null) {
      return 0;
    }
    switch(this.calcPosition()) {
      case "top":
      case "bottom":
        return this.getAnchor().getX() + this.getAnchor().getWidth() / 2 - this.getWidth() / 2;
      case "right":
        return this.getAnchor().getX() + this.getAnchor().getWidth() + this.getOffset();
      case "left":
        return this.getAnchor().getX() - this.getWidth() - this.getOffset();
    }
    return 0;
  }

  @Override
  public float getY() {
    if (this.getAnchor() == null) {
      return 0;
    }
    switch(this.calcPosition()) {
      case "top":
        return this.getAnchor().getY() + this.getHeight() + this.getOffset();
      case "bottom":
        return this.getAnchor().getY() - this.getAnchor().getHeight() - this.getOffset();
      case "left":
      case "right":
        return this.getAnchor().getY() - this.getAnchor().getHeight() / 2 + this.getHeight() / 2;
    }
    return 0;
  }

  private String calcPosition() {
    String[] order = {"top", "bottom", "right", "left"};
    if (this.willFit(this.getPosition())) {
      return this.getPosition();
    }
    for (String pos : order) {
      if (pos.equals(this.getPosition())) {
        continue;
      }
      if (this.willFit(pos)) {
        return pos;
      }
    }
    return this.getPosition();
  }

  private boolean willFit(String position) {
    float maxX = Renderer.unitsWide / 2;
    float maxY = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide) / 2;
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
