package bin.graphics.ui.colorUtils;

import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UIElement;
import bin.graphics.Color;
import bin.graphics.objects.HueRect;
import bin.graphics.objects.RoundedBoxOutline;
import bin.graphics.objects.Global;

public class UIHueColorBar extends UIBoxCol {

  //bottom left, bottom right, top right, top left

  public UIHueColorBar(UIElement parent) {
    super(parent);
  }

  @Override
  public void render() {
    if (!this.noBackground) {
      HueRect.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight(), 0f);
    }
    if (this.outlineWeight > 0f) {
      Global.drawColor(this.getOutlineColor());
      RoundedBoxOutline.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight(), 0f, this.outlineWeight);
    }
    for (int i = 0; i < this.children.size(); i++) {
      this.children.get(i).render();
    }
  }
}
