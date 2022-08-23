package bin.graphics.ui.complex;

import bin.input.IClickable;
import bin.graphics.ui.complex.UIPopUp;
import bin.graphics.ui.UIElement;

public class UIToolTip extends UIPopUp{

  public UIToolTip(UIElement parent) {
    super(parent);
  }

  @Override
  public void render() {
    if (this.getAnchor() instanceof IClickable && ((IClickable)this.getAnchor()).getIsMousedOver()) {
      super.render();
    }
  }

}
