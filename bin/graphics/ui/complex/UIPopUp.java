package bin.graphics.ui.complex;

import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UIElement;
import bin.graphics.Renderer;

public class UIPopUp extends UIBoxCol {

  public UIElement anchor;
  public float offset;
  public String position;

  @Override
  public UIPopUp(UIElement parent) {
    super(parent);
    this.anchor = null;
    this.offset = 0;
    this.position = top;
  }

  private String calcPosition() {

  }

  private boolean willFit(String position) {
    switch(position) {
      case "top":

    }
  }

  private float neededSlide(String position) {

  }
}
