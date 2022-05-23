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
    if (this.anchor instanceof IClickable && ((IClickable)this.anchor).getIsMousedOver()) {
      super.render();
    }
  }

}
